/*****************************************************************************
 * audio_output.h : audio output thread interface
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 * $Id: audio_output.h,v 1.45 2002/03/26 23:08:40 gbazin Exp $
 *
 * Authors: Michel Kaempf <maxx@via.ecp.fr>
 *          Cyril Deguet <asmax@via.ecp.fr>
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
 * aout_bank_t, p_aout_bank (global variable)
 *****************************************************************************
 * This global variable is accessed by any function using the audio output.
 *****************************************************************************/
typedef struct aout_bank_s
{
    /* Array to all the audio outputs */
    struct aout_thread_s *pp_aout[ AOUT_MAX_THREADS ];

    int                    i_count;
    vlc_mutex_t            lock;  /* Global lock */

} aout_bank_t;

#ifndef PLUGIN
extern aout_bank_t *p_aout_bank;
#else
#   define p_aout_bank (p_symbols->p_aout_bank)
#endif

/*****************************************************************************
 * aout_increment_t
 *****************************************************************************
 * This structure is used to keep the progression of an index up-to-date, in
 * order to avoid rounding problems and heavy computations, as the function
 * that handles this structure only uses additions.
 *****************************************************************************/
typedef struct aout_increment_s
{
    /* The remainder is used to keep track of the fractional part of the
     * index. */
    int i_r;

    /*
     * The increment structure is initialized with the result of an euclidean
     * division :
     *
     *  i_x           i_b
     * ----- = i_a + -----
     *  i_y           i_c
     *
     */
    int i_a, i_b, i_c;

} aout_increment_t;

/*****************************************************************************
 * aout_fifo_t
 *****************************************************************************/
typedef struct aout_fifo_s
{
    /* See the fifo formats below */
    int                 i_format;
    int                 i_channels;
    int                 i_rate;
    int                 i_frame_size;

    boolean_t           b_die;
    int                 i_fifo;      /* Just to keep track of the fifo index */

    vlc_mutex_t         data_lock;
    vlc_cond_t          data_wait;

    u8 *                buffer;
    mtime_t *           date;

    /* The start frame is the first frame in the buffer that contains decoded
     * audio data. It it also the first frame in the current timestamped frame
     * area, ie the first dated frame in the decoded part of the buffer. :-p */
    int                 i_start_frame;
    boolean_t           b_start_frame;
    /* The next frame is the end frame of the current timestamped frame area,
     * ie the first dated frame after the start frame. */
    int                 i_next_frame;
    boolean_t           b_next_frame;
    /* The end frame is the first frame, after the start frame, that doesn't
     * contain decoded audio data. That's why the end frame is the first frame
     * where the audio decoder can store its decoded audio frames. */
    int                 i_end_frame;

    /* Current index in p_aout->buffer */
    int                 i_unit;
    /* Max index in p_aout->buffer */
    int                 i_unit_limit;
    /* Structure used to calculate i_unit with a Bresenham algorithm */
    aout_increment_t    unit_increment;

    /* The following variable is used to store the number of remaining audio
     * units in the current timestamped frame area. */
    int                 i_units;

} aout_fifo_t;

#define AOUT_FIFO_ISEMPTY( fifo ) \
  ( (fifo).i_end_frame == (fifo).i_start_frame )

#define AOUT_FIFO_ISFULL( fifo ) \
  ( ((((fifo).i_end_frame + 1) - (fifo).i_start_frame) & AOUT_FIFO_SIZE) == 0 )

#define AOUT_FIFO_INC( i_index ) \
  ( ((i_index) + 1) & AOUT_FIFO_SIZE )

/* List of known fifo formats */
#define AOUT_FIFO_NONE    0
#define AOUT_FIFO_PCM     1
#define AOUT_FIFO_SPDIF   2

/*****************************************************************************
 * aout_thread_t : audio output thread descriptor
 *****************************************************************************/
typedef struct aout_thread_s
{
    vlc_thread_t        thread_id;
    boolean_t           b_die;
    boolean_t           b_active;

    vlc_mutex_t         fifos_lock;
    aout_fifo_t         fifo[ AOUT_MAX_FIFOS ];

    /* Plugin used and shortcuts to access its capabilities */
    struct module_s *   p_module;
    int              ( *pf_open )       ( p_aout_thread_t );
    int              ( *pf_setformat )  ( p_aout_thread_t );
    int              ( *pf_getbufinfo ) ( p_aout_thread_t, int );
    void             ( *pf_play )       ( p_aout_thread_t, byte_t *, int );
    void             ( *pf_close )      ( p_aout_thread_t );

    void *              buffer;
    /* The s32 buffer is used to mix all the audio fifos together before
     * converting them and storing them in the audio output buffer */
    s32 *               s32_buffer;

    /* The size of the audio output buffer is kept in audio units, as this is
     * the only unit that is common with every audio decoder and audio fifo */
    int                 i_units;

    /* date is the moment where the first audio unit of the output buffer
     * will be played */
    mtime_t             date;

    /* The current volume */
    int                 i_volume;
    int                 i_savedvolume;

    /* Format of the audio output samples, number of channels, and
     * rate and gain (in Hz) of the audio output sound */
    int                 i_format;
    int                 i_channels;
    int                 i_rate;

    /* Latency of the audio output plugin, in bytes */
    int                 i_latency;

    /* there might be some useful private structure, such as audio_buf_info
     * for the OSS output */
    p_aout_sys_t        p_sys;

} aout_thread_t;

/* Those are from <linux/soundcard.h> but are needed because of formats
 * on other platforms */
#define AOUT_FMT_U8          0x00000008
#define AOUT_FMT_S16_LE      0x00000010           /* Little endian signed 16 */
#define AOUT_FMT_S16_BE      0x00000020              /* Big endian signed 16 */
#define AOUT_FMT_S8          0x00000040
#define AOUT_FMT_U16_LE      0x00000080                 /* Little endian U16 */
#define AOUT_FMT_U16_BE      0x00000100                    /* Big endian U16 */
#define AOUT_FMT_AC3         0x00000400                 /* Dolby Digital AC3 */

#ifdef WORDS_BIGENDIAN
#define AOUT_FMT_S16_NE      AOUT_FMT_S16_BE
#else
#define AOUT_FMT_S16_NE      AOUT_FMT_S16_LE
#endif

/* Number of samples in an AC3 frame */
#define AC3_FRAME_SIZE      1536

/* Size of a frame for spdif output */
#define SPDIF_FRAME_SIZE    6144

/*****************************************************************************
 * Prototypes
 *****************************************************************************/
#ifndef PLUGIN
void            aout_InitBank           ( void );
void            aout_EndBank            ( void );

aout_thread_t * aout_CreateThread       ( int *, int, int );
void            aout_DestroyThread      ( aout_thread_t *, int * );

aout_fifo_t *   aout_CreateFifo         ( int, int, int, int, void * );
void            aout_DestroyFifo        ( aout_fifo_t *p_fifo );
void            aout_FreeFifo           ( aout_fifo_t *p_fifo );
#else
#   define aout_CreateFifo p_symbols->aout_CreateFifo
#   define aout_DestroyFifo p_symbols->aout_DestroyFifo
#endif

