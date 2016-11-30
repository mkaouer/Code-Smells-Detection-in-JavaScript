/*****************************************************************************
 * generic_decoder.h : generic decoder thread
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 *
 * Authors:
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
 *****************************************************************************

/*****************************************************************************
 * Requires:
 *  "config.h"
 *  "common.h"
 *  "mtime.h"
 *  "threads.h"
 *  "input.h"
 *  "decoder_fifo.h"
 * XXX??
 *****************************************************************************/

/*****************************************************************************
 * gdec_cfg_t: generic decoder configuration structure
 *****************************************************************************
 * This structure is passed as an initializer when a generic decoder thread is
 * created.
 *****************************************************************************/
typedef struct gdec_cfg_s
{
    u64         i_properties;

    int         i_actions;                                /* decoder actions */
    char *      psz_base_filename;                   /* base demux file name */
} gdec_cfg_t;

/* Properties flags */
#define GDEC_CFG_ACTIONS    (1 << 0)
#define GDEC_CFG_FILENAME   (1 << 1)

/*****************************************************************************
 * gdec_thread_t: generic decoder thread descriptor
 *****************************************************************************
 * This type describes a generic decoder thread.
 *****************************************************************************/
typedef struct gdec_thread_s
{
    /* Thread properties and locks */
    boolean_t           b_die;                                 /* `die' flag */
    boolean_t           b_error;                             /* `error' flag */
    boolean_t           b_active;                           /* `active' flag */
    vlc_thread_t        thread_id;                /* id for thread functions */

    /* Thread configuration */
    int                 i_actions;                        /* decoder actions */

    /* Input properties */
    input_thread_t *    p_input;                             /* input thread */
    decoder_fifo_t      fifo;                              /* PES input fifo */

    /* XXX?? status info */
    int *               pi_status;


    /* Files array - these files are used to extract ES streams from a
     * demultiplexed stream */
    /* XXX?? */

#ifdef STATS
    /* Statistics */
    count_t         c_loops;                              /* number of loops */
    count_t         c_idle_loops;                    /* number of idle loops */
    count_t         c_pes;                     /* number of PES packets read */
#endif
} gdec_thread_t;

/* Decoder actions - this flags select which actions the decoder will perform
 * when it receives a PES packet */
#define GDEC_IDENTIFY   (1 << 0)                 /* update input's ES tables */
#define GDEC_SAVE       (1 << 1)              /* save all PES to a same file */
#define GDEC_SAVE_DEMUX (1 << 2)           /* save PES to files by stream id */
#define GDEC_PRINT      (1 << 3)                   /* print PES informations */

/*****************************************************************************
 * Prototypes
 *****************************************************************************/

/* Thread management functions */
gdec_thread_t * gdec_CreateThread       ( gdec_cfg_t *p_cfg,
                                          input_thread_t *p_input, int *pi_status );
void            gdec_DestroyThread      ( gdec_thread_t *p_gdec, int *pi_status );

/* Time management functions */
/* XXX?? */

/* Dynamic thread settings */
/* XXX?? */
