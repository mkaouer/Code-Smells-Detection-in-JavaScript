/*****************************************************************************
 * ac3_decoder.h : ac3 decoder interface
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 *
 * Authors:
 * Michel Kaempf <maxx@via.ecp.fr>
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

/**** ac3 decoder API - public ac3 decoder structures */

typedef struct ac3dec_s ac3dec_t;

typedef struct ac3_sync_info_s {
    int sample_rate;    /* sample rate in Hz */
    int frame_size;     /* frame size in bytes */
    int bit_rate;       /* nominal bit rate in kbps */
} ac3_sync_info_t;

typedef struct ac3_byte_stream_s {
    u8 * p_byte;
    u8 * p_end;
    void * info;
} ac3_byte_stream_t;

/**** ac3 decoder API - functions publically provided by the ac3 decoder ****/

int ac3_init (ac3dec_t * p_ac3dec);
int ac3_sync_frame (ac3dec_t * p_ac3dec, ac3_sync_info_t * p_sync_info);
int ac3_decode_frame (ac3dec_t * p_ac3dec, s16 * buffer);
static ac3_byte_stream_t * ac3_byte_stream (ac3dec_t * p_ac3dec);

/**** ac3 decoder API - user functions to be provided to the ac3 decoder ****/

void ac3_byte_stream_next (ac3_byte_stream_t * p_byte_stream);

/**** EVERYTHING AFTER THIS POINT IS PRIVATE ! DO NOT USE DIRECTLY ****/

/**** ac3 decoder internal structures ****/

/* The following structures are filled in by their corresponding parse_*
 * functions. See http://www.atsc.org/Standards/A52/a_52.pdf for
 * full details on each field. Indented fields are used to denote
 * conditional fields.
 */

typedef struct syncinfo_s {
    /* Sync word == 0x0B77 */
    /* u16 syncword; */
    /* crc for the first 5/8 of the sync block */
    /* u16 crc1; */
    /* Stream Sampling Rate (kHz) 0 = 48, 1 = 44.1, 2 = 32, 3 = reserved */
    u16 fscod;
    /* Frame size code */
    u16 frmsizecod;

    /* Information not in the AC-3 bitstream, but derived */
    /* Frame size in 16 bit words */
    u16 frame_size;
    /* Bit rate in kilobits */
    //u16 bit_rate;
} syncinfo_t;

typedef struct bsi_s {
    /* Bit stream identification == 0x8 */
    u16 bsid;
    /* Bit stream mode */
    u16 bsmod;
    /* Audio coding mode */
    u16 acmod;
    /* If we're using the centre channel then */
        /* centre mix level */
        u16 cmixlev;
    /* If we're using the surround channel then */
        /* surround mix level */
        u16 surmixlev;
    /* If we're in 2/0 mode then */
        /* Dolby surround mix level - NOT USED - */
        u16 dsurmod;
    /* Low frequency effects on */
    u16 lfeon;
    /* Dialogue Normalization level */
    u16 dialnorm;
    /* Compression exists */
    u16 compre;
        /* Compression level */
        u16 compr;
    /* Language code exists */
    u16 langcode;
        /* Language code */
        u16 langcod;
    /* Audio production info exists*/
    u16 audprodie;
        u16 mixlevel;
        u16 roomtyp;
    /* If we're in dual mono mode (acmod == 0) then extra stuff */
        u16 dialnorm2;
        u16 compr2e;
            u16 compr2;
        u16 langcod2e;
            u16 langcod2;
        u16 audprodi2e;
            u16 mixlevel2;
            u16 roomtyp2;
    /* Copyright bit */
    u16 copyrightb;
    /* Original bit */
    u16 origbs;
    /* Timecode 1 exists */
    u16 timecod1e;
        /* Timecode 1 */
        u16 timecod1;
    /* Timecode 2 exists */
    u16 timecod2e;
        /* Timecode 2 */
        u16 timecod2;
    /* Additional bit stream info exists */
    u16 addbsie;
        /* Additional bit stream length - 1 (in bytes) */
        u16 addbsil;
        /* Additional bit stream information (max 64 bytes) */
        u8 addbsi[64];

    /* Information not in the AC-3 bitstream, but derived */
    /* Number of channels (excluding LFE)
     * Derived from acmod */
    u16 nfchans;
} bsi_t;

/* more pain */
typedef struct audblk_s {
    /* block switch bit indexed by channel num */
    u16 blksw[5];
    /* dither enable bit indexed by channel num */
    u16 dithflag[5];
    /* dynamic range gain exists */
    u16 dynrnge;
        /* dynamic range gain */
        u16 dynrng;
    /* if acmod==0 then */
    /* dynamic range 2 gain exists */
    u16 dynrng2e;
        /* dynamic range 2 gain */
        u16 dynrng2;
    /* coupling strategy exists */
    u16 cplstre;
        /* coupling in use */
        u16 cplinu;
            /* channel coupled */
            u16 chincpl[5];
            /* if acmod==2 then */
                /* Phase flags in use */
                u16 phsflginu;
            /* coupling begin frequency code */
            u16 cplbegf;
            /* coupling end frequency code */
            u16 cplendf;
            /* coupling band structure bits */
            u16 cplbndstrc[18];
            /* Do coupling co-ords exist for this channel? */
            u16 cplcoe[5];
            /* Master coupling co-ordinate */
            u16 mstrcplco[5];
            /* Per coupling band coupling co-ordinates */
            u16 cplcoexp[5][18];
            u16 cplcomant[5][18];
            /* Phase flags for dual mono */
            u16 phsflg[18];
    /* Is there a rematrixing strategy */
    u16 rematstr;
        /* Rematrixing bits */
        u16 rematflg[4];
    /* Coupling exponent strategy */
    u16 cplexpstr;
    /* Exponent strategy for full bandwidth channels */
    u16 chexpstr[5];
    /* Exponent strategy for lfe channel */
    u16 lfeexpstr;
    /* Channel bandwidth for independent channels */
    u16 chbwcod[5];
        /* The absolute coupling exponent */
        u16 cplabsexp;
        /* Coupling channel exponents (D15 mode gives 18 * 12 /3  encoded exponents */
        u16 cplexps[18 * 12 / 3];
    /* Sanity checking constant */
    u32 magic2;
    /* fbw channel exponents */
    u16 exps[5][252 / 3];
    /* channel gain range */
    u16 gainrng[5];
    /* low frequency exponents */
    u16 lfeexps[3];

    /* Bit allocation info */
    u16 baie;
        /* Slow decay code */
        u16 sdcycod;
        /* Fast decay code */
        u16 fdcycod;
        /* Slow gain code */
        u16 sgaincod;
        /* dB per bit code */
        u16 dbpbcod;
        /* masking floor code */
        u16 floorcod;

    /* SNR offset info */
    u16 snroffste;
        /* coarse SNR offset */
        u16 csnroffst;
        /* coupling fine SNR offset */
        u16 cplfsnroffst;
        /* coupling fast gain code */
        u16 cplfgaincod;
        /* fbw fine SNR offset */
        u16 fsnroffst[5];
        /* fbw fast gain code */
        u16 fgaincod[5];
        /* lfe fine SNR offset */
        u16 lfefsnroffst;
        /* lfe fast gain code */
        u16 lfefgaincod;

    /* Coupling leak info */
    u16 cplleake;
        /* coupling fast leak initialization */
        u16 cplfleak;
        /* coupling slow leak initialization */
        u16 cplsleak;

    /* delta bit allocation info */
    u16 deltbaie;
        /* coupling delta bit allocation exists */
        u16 cpldeltbae;
        /* fbw delta bit allocation exists */
        u16 deltbae[5];
        /* number of cpl delta bit segments */
        u16 cpldeltnseg;
            /* coupling delta bit allocation offset */
            u16 cpldeltoffst[8];
            /* coupling delta bit allocation length */
            u16 cpldeltlen[8];
            /* coupling delta bit allocation length */
            u16 cpldeltba[8];
        /* number of delta bit segments */
        u16 deltnseg[5];
            /* fbw delta bit allocation offset */
            u16 deltoffst[5][8];
            /* fbw delta bit allocation length */
            u16 deltlen[5][8];
            /* fbw delta bit allocation length */
            u16 deltba[5][8];

    /* skip length exists */
    u16 skiple;
        /* skip length */
        u16 skipl;

    /* channel mantissas */
//      u16 chmant[5][256];

    /* coupling mantissas */
    float cplfbw[ 256 ];
//      u16 cplmant[256];

    /* coupling mantissas */
//      u16 lfemant[7];

    /* -- Information not in the bitstream, but derived thereof -- */

    /* Number of coupling sub-bands */
    u16 ncplsubnd;

    /* Number of combined coupling sub-bands
     * Derived from ncplsubnd and cplbndstrc */
    u16 ncplbnd;

    /* Number of exponent groups by channel
     * Derived from strmant, endmant */
    u16 nchgrps[5];

    /* Number of coupling exponent groups
     * Derived from cplbegf, cplendf, cplexpstr */
    u16 ncplgrps;

    /* End mantissa numbers of fbw channels */
    u16 endmant[5];

    /* Start and end mantissa numbers for the coupling channel */
    u16 cplstrtmant;
    u16 cplendmant;

    /* Decoded exponent info */
    u16 fbw_exp[5][256];
    u16 cpl_exp[256];
    u16 lfe_exp[7];

    /* Bit allocation pointer results */
    u16 fbw_bap[5][256];
    /* FIXME?? figure out exactly how many entries there should be (253-37?) */
    u16 cpl_bap[256];
    u16 lfe_bap[7];
} audblk_t;

/* Everything you wanted to know about band structure */
/*
 * The entire frequency domain is represented by 256 real
 * floating point fourier coefficients. Only the lower 253
 * coefficients are actually utilized however. We use arrays
 * of 256 to be efficient in some cases.
 *
 * The 5 full bandwidth channels (fbw) can have their higher
 * frequencies coupled together. These coupled channels then
 * share their high frequency components.
 *
 * This coupling band is broken up into 18 sub-bands starting
 * at mantissa number 37. Each sub-band is 12 bins wide.
 *
 * There are 50 bit allocation sub-bands which cover the entire
 * frequency range. The sub-bands are of non-uniform width, and
 * approximate a 1/6 octave scale.
 */

typedef struct stream_coeffs_s
{
    float fbw[5][256];
    float lfe[256];
} stream_coeffs_t;

typedef struct stream_samples_s
{
    float channel[6][256];
} stream_samples_t;

typedef struct ac3_bit_stream_s
{
    u32 buffer;
    int i_available;
    ac3_byte_stream_t byte_stream;

    unsigned int total_bits_read; /* temporary */
} ac3_bit_stream_t;

struct ac3dec_s
{
    /*
     * Input properties
     */

    /* The bit stream structure handles the PES stream at the bit level */
    ac3_bit_stream_t    bit_stream;

    /*
     * Decoder properties
     */
    syncinfo_t          syncinfo;
    bsi_t               bsi;
    audblk_t            audblk;

    stream_coeffs_t     coeffs;
    stream_samples_t    samples;
};

/**** ac3 decoder inline functions ****/

static ac3_byte_stream_t * ac3_byte_stream (ac3dec_t * p_ac3dec)
{
    return &(p_ac3dec->bit_stream.byte_stream);
}
