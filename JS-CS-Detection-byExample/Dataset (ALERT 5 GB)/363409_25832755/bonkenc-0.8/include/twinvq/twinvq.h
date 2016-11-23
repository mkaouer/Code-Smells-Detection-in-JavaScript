/* (c)Copyright 1996-2000 NTT Cyber Space Laboratories */
/*                Released on 2000.05.22 by N. Iwakami */
/*                Modified on 2000.05.25 by N. Iwakami */
/*                Released on 2000.09.06 by N. Iwakami */

#ifndef twinvq_h
#define twinvq_h

#include <stdio.h>

/************************/
/*** General settings ***/
/************************/
/* Initialization error code */
enum INIT_ERROR_CODE {
	TVQ_NO_ERROR = 0,     // no error
	TVQ_ERROR,            // general
	TVQ_ERROR_VERSION,    // wrong version
	TVQ_ERROR_CHANNEL,    // channel setting error
	TVQ_ERROR_MODE,       // wrong coding mode
	TVQ_ERROR_PARAM,      // inner parameter setting error
	TVQ_ERROR_N_CAN,      // wrong number of VQ pre-selection candidates, used only in encoder
};

/* version ID */
#define TVQ_UNKNOWN_VERSION  -1
#define V2                    0
#define V2PP                  1

#define N_VERSIONS            2

/* window types */
enum WINDOW_TYPE {
  ONLY_LONG_WINDOW = 0,
  LONG_SHORT_WINDOW,
  ONLY_SHORT_WINDOW,
  SHORT_LONG_WINDOW,
  SHORT_MEDIUM_WINDOW,
  MEDIUM_LONG_WINDOW,
  LONG_MEDIUM_WINDOW,
  MEDIUM_SHORT_WINDOW,
  ONLY_MEDIUM_WINDOW,
};

/* block types */
enum BLOCK_TYPE {
	BLK_SHORT = 0,
	BLK_MEDIUM,
	BLK_LONG,
	BLK_PPC,
};
#define N_BTYPE     3  // number of block types
#define N_INTR_TYPE 4  // number of interleave types, enum BLOCK_TYPE is commonly used for detecting interleave types.

/* maximum number of channels */
#define N_CH_MAX     2

/* type definition of code information interface */
typedef struct {
	/* block type */
    int   w_type;
	int   btype;

	/* FBC info */
    int   *segment_sw[ N_CH_MAX ];
    int      *band_sw[ N_CH_MAX ];
    int *fg_intensity[ N_CH_MAX ];

	/* VQ info */
	int   *wvq;

	/* BSE info */
    int   *fw;
    int   *fw_alf;

	/* gain info */
    int   *pow;

	/* LSP info */
    int   *lsp[ N_CH_MAX ];

	/* PPC info */
    int     pit[ N_CH_MAX ];
    int   *pls;
    int   pgain[ N_CH_MAX ];

	/* EBC info */
	int   *bc[ N_CH_MAX ];

	void *manager;
} INDEX;

/***********************************************/
/*** Definitions about program configuration ***/
/***********************************************/
/* type definition of tvqConfInfoSubBlock */
typedef struct {
	int sf_sz;         // subframe size
	int nsf;           // number of subframes
	int ndiv;          // number of division of weighted interleave vector quantization
	int ncrb;          // number of Bark-scale subbands
	int fw_ndiv;       // number of division of BSE VQ
	int fw_nbit;       // number of bits for BSE VQ
	int nsubg;         // number of sub-blocks for gain coding
	int ppc_enable;    // PPC switch
	int ebc_enable;    // EBC switch
	int ebc_crb_base;  // EBC base band
	int ebc_bits;      // EBC bits
	int fbc_enable;    // FBC switch
	int fbc_n_segment; // FBC number of segments
	int fbc_nband;     // FBC number of subbands
	int *fbc_crb_tbl;  // FBC subband table
} tvqConfInfoSubBlock;

/* type definition of tvqConfInfo */
typedef struct {
  /* frame configuration */
  int N_CH;
  /* window type coding */
  int BITS_WTYPE;
  /* LSP coding */
  int LSP_BIT0;
  int LSP_BIT1;
  int LSP_BIT2;
  int LSP_SPLIT;
  /* Bark-scale envelope coding */
  int FW_ARSW_BITS;
  /* gain coding */
  int GAIN_BITS;
  int SUB_GAIN_BITS;
  /* pitch excitation */
  int N_DIV_P;
  int BASF_BIT;
  int PGAIN_BIT;

  /* block type dependent parameters */
  tvqConfInfoSubBlock cfg[N_BTYPE];

} tvqConfInfo;


/*************************************************/
/*** Definitions about TwinVQ bitstream header ***/
/*************************************************/
//#include "../declib_src/tvq_hdr.h"
//#ifndef	BUFSIZ
//#define	BUFSIZ		1024
//#endif

#define	KEYWORD_BYTES	4
#define	VERSION_BYTES	8
#define ELEM_BYTES      sizeof(unsigned long)


/*
 */
typedef struct{
	char		ID[KEYWORD_BYTES+VERSION_BYTES+1];
	int size;
	/* Common Chunk */
	int channelMode;   /* channel mode (mono:0/stereo:1) */
	int bitRate;       /* bit rate (kbit/s) */
	int samplingRate;  /* sampling rate (44.1 kHz -> 44) */
	int securityLevel; /* security level (always 0) */
	/* Text Chunk */
	char	Name[BUFSIZ];
	char	Comt[BUFSIZ];
	char	Auth[BUFSIZ];
	char	Cpyr[BUFSIZ];
	char	File[BUFSIZ];
	char	Extr[BUFSIZ];  // add by OKAMOTO 99.12.21
	/* Data size chunk*/
	int		Dsiz;
} headerInfo;


#endif

