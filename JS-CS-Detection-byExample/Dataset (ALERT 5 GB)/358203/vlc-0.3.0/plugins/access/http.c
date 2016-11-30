/*****************************************************************************
 * http.c: HTTP access plug-in
 *****************************************************************************
 * Copyright (C) 2001, 2002 VideoLAN
 * $Id: http.c,v 1.7 2002/04/03 23:24:42 massiot Exp $
 *
 * Authors: Christophe Massiot <massiot@via.ecp.fr>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111, USA.
 *****************************************************************************/

/*****************************************************************************
 * Preamble
 *****************************************************************************/
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>

#include <videolan/vlc.h>

#ifdef HAVE_UNISTD_H
#   include <unistd.h>
#elif defined( _MSC_VER ) && defined( _WIN32 )
#   include <io.h>
#endif

#include "stream_control.h"
#include "input_ext-intf.h"
#include "input_ext-dec.h"
#include "input_ext-plugins.h"

#include "network.h"

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/
static void input_getfunctions( function_list_t * );
static int  HTTPOpen       ( struct input_thread_s * );
static void HTTPClose      ( struct input_thread_s * );
static int  HTTPSetProgram ( struct input_thread_s * , pgrm_descriptor_t * );  
static void HTTPSeek       ( struct input_thread_s *, off_t );

/*****************************************************************************
 * Build configuration tree.
 *****************************************************************************/
MODULE_CONFIG_START
MODULE_CONFIG_STOP
 
MODULE_INIT_START
    SET_DESCRIPTION( "HTTP access plug-in" )
    ADD_CAPABILITY( ACCESS, 0 )
    ADD_SHORTCUT( "http" )
    ADD_SHORTCUT( "http4" )
    ADD_SHORTCUT( "http6" )
MODULE_INIT_STOP
 
MODULE_ACTIVATE_START
    input_getfunctions( &p_module->p_functions->access );
MODULE_ACTIVATE_STOP
 
MODULE_DEACTIVATE_START
MODULE_DEACTIVATE_STOP

/*****************************************************************************
 * Functions exported as capabilities. They are declared as static so that
 * we don't pollute the namespace too much.
 *****************************************************************************/
static void input_getfunctions( function_list_t * p_function_list )
{
#define input p_function_list->functions.access
    input.pf_open             = HTTPOpen;
    input.pf_read             = input_FDNetworkRead;
    input.pf_close            = HTTPClose;
    input.pf_set_program      = HTTPSetProgram;
    input.pf_set_area         = NULL;
    input.pf_seek             = HTTPSeek;
#undef input
}

/*****************************************************************************
 * _input_socket_t: private access plug-in data, modified to add private
 *                  fields
 *****************************************************************************/
typedef struct _input_socket_s
{
    input_socket_t      _socket;

    char *              psz_network;
    network_socket_t    socket_desc;
    char                psz_buffer[256];
    char *              psz_name;
} _input_socket_t;

/*****************************************************************************
 * HTTPConnect: connect to the server and seek to i_tell
 *****************************************************************************/
static int HTTPConnect( input_thread_t * p_input, off_t i_tell )
{
    _input_socket_t *   p_access_data = p_input->p_access_data;
    struct module_s *   p_network;
    char                psz_buffer[256];
    byte_t *            psz_parser;

    /* Find an appropriate network module */
    p_network = module_Need( MODULE_CAPABILITY_NETWORK,
                             p_access_data->psz_network,
                             &p_access_data->socket_desc );
    if( p_network == NULL )
    {
        free( p_access_data );
        return( -1 );
    }
    module_Unneed( p_network );

    p_access_data->_socket.i_handle = p_access_data->socket_desc.i_handle;

#   define HTTP_USERAGENT "User-Agent: " COPYRIGHT_MESSAGE "\r\n"
#   define HTTP_END       "\r\n"
 
    snprintf( psz_buffer, sizeof(psz_buffer),
              "%s"
              "Range: bytes=%lld-\r\n"
              HTTP_USERAGENT HTTP_END,
              p_access_data->psz_buffer, i_tell );
    psz_buffer[sizeof(psz_buffer) - 1] = '\0';

    /* Send GET ... */
    if( write( p_access_data->_socket.i_handle, psz_buffer,
               strlen( psz_buffer ) ) == (-1) )
    {
        intf_ErrMsg( "http error: cannot send request (%s)", strerror(errno) );
        input_FDClose( p_input );
        return( -1 );
    }

    /* Prepare the input thread for reading. */ 
    p_input->i_bufsize = INPUT_DEFAULT_BUFSIZE;
    /* FIXME: we shouldn't have to do that ! */
    p_input->pf_read = input_FDNetworkRead;

    while( !input_FillBuffer( p_input ) )
    {
        if( p_input->b_die || p_input->b_error )
        {
            input_FDClose( p_input );
            return( -1 );
        }
    }

    /* Parse HTTP header. */
#define MAX_LINE 1024
    for( ; ; ) 
    {
        if( input_Peek( p_input, &psz_parser, MAX_LINE ) <= 0 )
        {
            intf_ErrMsg( "http error: not enough data" );
            input_FDClose( p_input );
            return( -1 );
        }

        if( psz_parser[0] == '\r' && psz_parser[1] == '\n' )
        {
            /* End of header. */
            p_input->p_current_data += 2;
            break;
        }

        if( !strncmp( psz_parser, "Content-Length: ",
                      strlen("Content-Length: ") ) )
        {
            psz_parser += strlen("Content-Length: ");
            /* FIXME : this won't work for 64-bit lengths */
            vlc_mutex_lock( &p_input->stream.stream_lock );
            p_input->stream.p_selected_area->i_size = atoi( psz_parser )
                                                        + i_tell;
            vlc_mutex_unlock( &p_input->stream.stream_lock );
        }

        while( *psz_parser != '\r' && psz_parser < p_input->p_last_data )
        {
            psz_parser++;
        }
        p_input->p_current_data = psz_parser + 2;
    }

    if( p_input->stream.p_selected_area->i_size )
    {
        vlc_mutex_lock( &p_input->stream.stream_lock );
        p_input->stream.p_selected_area->i_tell = i_tell
            + (p_input->p_last_data - p_input->p_current_data);
        p_input->stream.b_seekable = 1;
        p_input->stream.b_changed = 1;
        vlc_mutex_unlock( &p_input->stream.stream_lock );
    }

    return( 0 );
}

/*****************************************************************************
 * HTTPOpen: parse URL and open the remote file at the beginning
 *****************************************************************************/
static int HTTPOpen( input_thread_t * p_input )
{
    _input_socket_t *   p_access_data;
    char *              psz_name = strdup(p_input->psz_name);
    char *              psz_parser = psz_name;
    char *              psz_server_addr = "";
    char *              psz_server_port = "";
    char *              psz_path = "";
    char *              psz_proxy;
    int                 i_server_port = 0;

    p_access_data = p_input->p_access_data = malloc( sizeof(_input_socket_t) );
    if( p_access_data == NULL )
    {
        intf_ErrMsg( "http error: Out of memory" );
        free(psz_name);
        return( -1 );
    }

    p_access_data->psz_name = psz_name;
    p_access_data->psz_network = "";
    if( config_GetIntVariable( "ipv4" ) )
    {
        p_access_data->psz_network = "ipv4";
    }
    if( config_GetIntVariable( "ipv6" ) )
    {
        p_access_data->psz_network = "ipv6";
    }
    if( *p_input->psz_access )
    {
        /* Find out which shortcut was used */
        if( !strncmp( p_input->psz_access, "http6", 6 ) )
        {
            p_access_data->psz_network = "ipv6";
        }
        else if( !strncmp( p_input->psz_access, "http4", 6 ) )
        {
            p_access_data->psz_network = "ipv4";
        }
    }

    /* Parse psz_name syntax :
     * //<hostname>[:<port>][/<path>] */
    while( *psz_parser == '/' )
    {
        psz_parser++;
    }
    psz_server_addr = psz_parser;

    while( *psz_parser && *psz_parser != ':' && *psz_parser != '/' )
    {
        psz_parser++;
    }

    if ( *psz_parser == ':' )
    {
        *psz_parser = '\0';
        psz_parser++;
        psz_server_port = psz_parser;

        while( *psz_parser && *psz_parser != '/' )
        {
            psz_parser++;
        }
    }

    if( *psz_parser == '/' )
    {
        *psz_parser = '\0';
        psz_parser++;
        psz_path = psz_parser;
    }

    /* Convert port format */
    if( *psz_server_port )
    {
        i_server_port = strtol( psz_server_port, &psz_parser, 10 );
        if( *psz_parser )
        {
            intf_ErrMsg( "input error: cannot parse server port near %s",
                         psz_parser );
            free( p_input->p_access_data );
            free( psz_name );
            return( -1 );
        }
    }

    if( i_server_port == 0 )
    {
        i_server_port = 80;
    }

    if( !*psz_server_addr )
    {
        intf_ErrMsg( "input error: no server given" );
        free( p_input->p_access_data );
        free( psz_name );
        return( -1 );
    }

    /* Check proxy */
    if( (psz_proxy = getenv( "http_proxy" )) != NULL && *psz_proxy )
    {
        /* http://myproxy.mydomain:myport/ */
        int                 i_proxy_port = 0;
 
        /* Skip the protocol name */
        while( *psz_proxy && *psz_proxy != ':' )
        {
            psz_proxy++;
        }
 
        /* Skip the "://" part */
        while( *psz_proxy && (*psz_proxy == ':' || *psz_proxy == '/') )
        {
            psz_proxy++;
        }
 
        /* Found a proxy name */
        if( *psz_proxy )
        {
            char *psz_port = psz_proxy;
 
            /* Skip the hostname part */
            while( *psz_port && *psz_port != ':' && *psz_port != '/' )
            {
                psz_port++;
            }
 
            /* Found a port name */
            if( *psz_port )
            {
                char * psz_junk;
 
                /* Replace ':' with '\0' */
                *psz_port = '\0';
                psz_port++;
 
                psz_junk = psz_port;
                while( *psz_junk && *psz_junk != '/' )
                {
                    psz_junk++;
                }
 
                if( *psz_junk )
                {
                    *psz_junk = '\0';
                }
 
                if( *psz_port != '\0' )
                {
                    i_proxy_port = atoi( psz_port );
                }
            }
        }
        else
        {
            intf_ErrMsg( "input error: http_proxy environment variable is invalid !" );
            free( p_input->p_access_data );
            free( psz_name );
            return( -1 );
        }

        p_access_data->socket_desc.i_type = NETWORK_TCP;
        p_access_data->socket_desc.psz_server_addr = psz_proxy;
        p_access_data->socket_desc.i_server_port = i_proxy_port;

        snprintf( p_access_data->psz_buffer, sizeof(p_access_data->psz_buffer),
                  "GET http://%s:%d/%s HTTP/1.1\r\n",
                  psz_server_addr, i_server_port, psz_path );
    }
    else
    {
        /* No proxy, direct connection. */
        p_access_data->socket_desc.i_type = NETWORK_TCP;
        p_access_data->socket_desc.psz_server_addr = psz_server_addr;
        p_access_data->socket_desc.i_server_port = i_server_port;

        snprintf( p_access_data->psz_buffer, sizeof(p_access_data->psz_buffer),
                  "GET /%s HTTP/1.1\r\nHost: %s\r\n",
                  psz_path, psz_server_addr );
    }
    p_access_data->psz_buffer[sizeof(p_access_data->psz_buffer) - 1] = '\0';

    intf_WarnMsg( 2, "input: opening server=%s port=%d path=%s",
                  psz_server_addr, i_server_port, psz_path );

    vlc_mutex_lock( &p_input->stream.stream_lock );
    p_input->stream.b_pace_control = 1;
    p_input->stream.b_seekable = 0;
    p_input->stream.p_selected_area->i_tell = 0;
    p_input->stream.p_selected_area->i_size = 0;
    p_input->stream.i_method = INPUT_METHOD_NETWORK;
    vlc_mutex_unlock( &p_input->stream.stream_lock );
    p_input->i_mtu = 0;
 
    return( HTTPConnect( p_input, 0 ) );
}

/*****************************************************************************
 * HTTPClose: free unused data structures
 *****************************************************************************/
static void HTTPClose( input_thread_t * p_input )
{
    input_FDClose( p_input );
}

/*****************************************************************************
 * HTTPSetProgram: do nothing
 *****************************************************************************/
static int HTTPSetProgram( input_thread_t * p_input,
                           pgrm_descriptor_t * p_program )
{
    return( 0 );
}

/*****************************************************************************
 * HTTPSeek: close and re-open a connection at the right place
 *****************************************************************************/
static void HTTPSeek( input_thread_t * p_input, off_t i_pos )
{
    _input_socket_t *   p_access_data = p_input->p_access_data;
    close( p_access_data->_socket.i_handle );
    intf_WarnMsg( 2, "http: seeking to position %lld", i_pos );
    HTTPConnect( p_input, i_pos );
}

