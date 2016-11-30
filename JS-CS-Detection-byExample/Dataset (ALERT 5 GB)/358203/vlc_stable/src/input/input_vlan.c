/*****************************************************************************
 * input_vlan.c: vlan management library
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 *
 * Authors: Vincent Seguin <seguin@via.ecp.fr>
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
#include "defs.h"

#include <errno.h>                                                 /* ENOMEM */
#include <stdio.h>                                              /* sprintf() */
#include <unistd.h>                                               /* close() */
#include <string.h>                                   /* strerror(), bzero() */
#include <stdlib.h>                                                /* free() */
#include <sys/time.h>                             /* timeval */

#if defined(SYS_BSD) || defined(SYS_BEOS)
#include <netinet/in.h>                                    /* struct in_addr */
#include <sys/socket.h>                                   /* struct sockaddr */
#endif

#if defined(SYS_LINUX) || defined(SYS_BSD) || defined(SYS_GNU)
#include <arpa/inet.h>                           /* inet_ntoa(), inet_aton() */
#endif

#ifdef SYS_LINUX
#include <sys/ioctl.h>                                            /* ioctl() */
#include <net/if.h>                            /* interface (arch-dependent) */
#endif

#include "config.h"
#include "common.h"
#include "threads.h"
#include "mtime.h"
#include "plugins.h"
#include "netutils.h"
#include "input_vlan.h"
#include "intf_msg.h"

#include "main.h"

/*****************************************************************************
 * input_vlan_t: vlan library data
 *****************************************************************************
 * Store global vlan library data.
 *****************************************************************************/
typedef struct input_vlan_s
{
    int         i_vlan_id;                            /* current vlan number */
    mtime_t     last_change;                             /* last change date */
} input_vlan_t;

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/

/*****************************************************************************
 * input_VlanCreate: initialize global vlan method data
 *****************************************************************************
 * Initialize vlan input method global data. This function should be called
 * once before any input thread is created or any call to other input_Vlan*()
 * function is attempted.
 *****************************************************************************/
int input_VlanCreate( void )
{
#ifdef SYS_BEOS
    intf_ErrMsg( "error: vlans are not supported under beos\n" );
    return( 1 );
#else
    /* Allocate structure */
    p_main->p_vlan = malloc( sizeof( input_vlan_t ) );
    if( p_main->p_vlan == NULL )
    {
        intf_ErrMsg("error: %s\n", strerror(ENOMEM));
        return( 1 );
    }

    /* Initialize structure */
    p_main->p_vlan->i_vlan_id   = 0;
    p_main->p_vlan->last_change = 0;

    intf_Msg("VLANs initialized\n");
    return( 0 );
#endif /* SYS_BEOS */
}

/*****************************************************************************
 * input_VlanDestroy: free global vlan method data
 *****************************************************************************
 * Free resources allocated by input_VlanMethodInit. This function should be
 * called at the end of the program.
 *****************************************************************************/
void input_VlanDestroy( void )
{
    /* Return to default vlan */
    if( p_main->p_vlan->i_vlan_id != 0 )
    {
        input_VlanJoin( 0 );
    }

    /* Free structure */
    free( p_main->p_vlan );
}

/*****************************************************************************
 * input_VlanLeave: leave a vlan
 *****************************************************************************
 * This function tells the vlan library that the designed interface is no more
 * locked and than vlan changes can occur.
 *****************************************************************************/
void input_VlanLeave( int i_vlan_id )
{
    /* XXX?? */
}

/*****************************************************************************
 * input_VlanJoin: join a vlan
 *****************************************************************************
 * This function will try to join a vlan. If the relevant interface is already
 * on the good vlan, nothing will be done. Else, and if possible (if the
 * interface is not locked), the vlan server will be contacted and a change will
 * be requested. The function will block until the change is effective. Note
 * that once a vlan is no more used, it's interface should be unlocked using
 * input_VlanLeave().
 * Non 0 will be returned in case of error.
 *****************************************************************************/
int input_VlanJoin( int i_vlan_id )
{
#ifdef SYS_BEOS
    return( -1 );
#else
    int                 socket_cl;
    int                 fromlen;
    struct ifreq        interface;
    struct sockaddr_in  sa_server;
    struct sockaddr_in  sa_client;
    unsigned int        version = 12;
    char                mess[80];
    char                mess_length = 80;
    struct timeval     *date_cl;
    struct timeval      time;
    long unsigned int   date;
    int                 nbanswer;
    char                answer;
    fd_set              rfds;
    unsigned int 	rc;

    /* If last change is too recent, wait a while */
    if( mdate() - p_main->p_vlan->last_change < INPUT_VLAN_CHANGE_DELAY )
    {
        intf_Msg("Waiting before changing VLAN...\n");
        mwait( p_main->p_vlan->last_change + INPUT_VLAN_CHANGE_DELAY );
    }
    p_main->p_vlan->last_change = mdate();
    p_main->p_vlan->i_vlan_id = i_vlan_id;

    intf_Msg("Joining channel %d\n", i_vlan_id );

    /*      
     * Looking for information about the eth0 interface
     */
    interface.ifr_addr.sa_family=AF_INET;
    strcpy(interface.ifr_name,INPUT_IFACE_DEFAULT);
    
    
    /*
     * Initialysing the socket
     */
    socket_cl=socket(AF_INET,SOCK_DGRAM,0);

    
    /* 
     * Getting the server's information 
     */
    bzero(&sa_server,sizeof(struct sockaddr_in));
    sa_server.sin_family=AF_INET;
    sa_server.sin_port=htons(INPUT_VLAN_PORT_DEFAULT);
    inet_aton(INPUT_VLAN_SERVER_DEFAULT,&(sa_server.sin_addr));
    
    /*
     * Looking for the interface MAC address
     */
    ioctl(socket_cl,SIOCGIFHWADDR,&interface);
    intf_DbgMsg("VLANSERVER: macaddr == %2.2x:%2.2x:%2.2x:%2.2x:%2.2x:%2.2x\n",
        interface.ifr_hwaddr.sa_data[0] & 0xff,
        interface.ifr_hwaddr.sa_data[1] & 0xff,
        interface.ifr_hwaddr.sa_data[2] & 0xff,
        interface.ifr_hwaddr.sa_data[3] & 0xff,
        interface.ifr_hwaddr.sa_data[4] & 0xff,
        interface.ifr_hwaddr.sa_data[5] & 0xff);
    
    /*
     * Getting date of the client
     */
    date_cl=malloc(sizeof(struct timeval));
    if(date_cl==NULL)
    {
        intf_ErrMsg("VLANSERVER: unable to allocate memory\n");
    /*    return VS_R_MEMORY;*/
        return -1;
    }
    
    if (gettimeofday(date_cl,0)==-1)
        return -1;
    date=date_cl->tv_sec;
    free(date_cl);
    intf_DbgMsg("VLANSERVER: date %lu\n",date);


    /* 
     * Build of the message
     */
    sprintf(mess,"%d %u %lu %2.2x:%2.2x:%2.2x:%2.2x:%2.2x:%2.2x \n",
          i_vlan_id, version, date,
        interface.ifr_hwaddr.sa_data[0] & 0xff, 
        interface.ifr_hwaddr.sa_data[1] & 0xff,
        interface.ifr_hwaddr.sa_data[2] & 0xff,
        interface.ifr_hwaddr.sa_data[3] & 0xff,
        interface.ifr_hwaddr.sa_data[4] & 0xff,
        interface.ifr_hwaddr.sa_data[5] & 0xff);
 
    intf_DbgMsg("VLANSERVER: The message is %s\n",mess);


    /*
     * Open the socket 2
     */
    bzero(&sa_client,sizeof(struct sockaddr_in));
    sa_client.sin_family=AF_INET;
    sa_client.sin_port=htons(4312);
    sa_client.sin_addr.s_addr=INADDR_ANY;
    fromlen=sizeof(struct sockaddr);
    rc=bind(socket_cl,(struct sockaddr *)(&sa_client),sizeof(struct sockaddr));
    if (rc)
    {
        intf_ErrMsg("VLANSERVER: Unable to bind socket:%u\n",rc); 
    /* TODO mettre un VS_R_BIND dans types.h*/
    /*    return VS_R_SOCKET;*/
        return -1;
    }


    /*
     * Send the message
     */
    sendto(socket_cl,mess,mess_length,0,(struct sockaddr *)(&sa_server),\
           sizeof(struct sockaddr));
   
     /*
     * Waiting 5 sec for one answer from the server
     */
    time.tv_sec=5;
    time.tv_usec=0;
    FD_ZERO(&rfds);
    FD_SET(socket_cl,&rfds);
    nbanswer=select(socket_cl+1,&rfds,NULL,NULL,&time);
    if(nbanswer==0)
        intf_DbgMsg("VLANSERVER: no answer\n");
    else if(nbanswer==-1)
        intf_DbgMsg("VLANSERVER: Unable to receive the answer\n");
    else
    {
        recvfrom(socket_cl,&answer,sizeof(char),0,\
                 (struct sockaddr *)(&sa_client),&fromlen);
        intf_DbgMsg("VLANSERVER: the answer : %hhd\n",answer);
        if(answer==-1)
            intf_DbgMsg("VLANSERVER: The server failed to create the thread\n");
        else if(answer==0)
            intf_DbgMsg("VLANSERVER: The server tries to change the channel\n");
        else
            intf_DbgMsg("VLANSERVER: Unknown answer !\n");
    }
    
    /*
     * Close the socket
     */
    close(socket_cl);

    return 0;

}
#endif
