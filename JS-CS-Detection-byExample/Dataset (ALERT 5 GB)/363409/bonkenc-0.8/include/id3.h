/* $Id: id3.h,v 1.17 2001/09/08 02:33:41 shadrack Exp $
 * 
 * id3lib: a software library for creating and manipulating id3v1/v2 tags
 * Copyright 1999, 2000  Scott Thomas Haug
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Library General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Library General Public
 * License for more details.

 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

 * The id3lib authors encourage improvements and optimisations to be sent to
 * the id3lib coordinator.  Please see the README file for details on where to
 * send such submissions.  See the AUTHORS file for a list of people who have
 * contributed to id3lib.  See the ChangeLog file for a list of changes to
 * id3lib.  These files are distributed with id3lib at
 * http://download.sourceforge.net/id3lib/
 */

#ifndef _ID3LIB_ID3_H_
#define _ID3LIB_ID3_H_

#include <id3/globals.h>

#ifdef __cplusplus
extern "C"
{
#endif /* __cplusplus */

  typedef struct { char _dummy; } ID3Tag;
  typedef struct { char _dummy; } ID3TagIterator;
  typedef struct { char _dummy; } ID3TagConstIterator;
  typedef struct { char _dummy; } ID3Frame;
  typedef struct { char _dummy; } ID3Field;
  typedef struct { char _dummy; } ID3FrameInfo;

  /* tag wrappers */
  ID3_C_EXPORT ID3Tag     *ID3Tag_New                  (void);
  ID3_C_EXPORT void        ID3Tag_Delete               (ID3Tag *tag);
  ID3_C_EXPORT void        ID3Tag_Clear                (ID3Tag *tag);
  ID3_C_EXPORT bool        ID3Tag_HasChanged           (const ID3Tag *tag);
  ID3_C_EXPORT void        ID3Tag_SetUnsync            (ID3Tag *tag, bool unsync);
  ID3_C_EXPORT void        ID3Tag_SetExtendedHeader    (ID3Tag *tag, bool ext);
  ID3_C_EXPORT void        ID3Tag_SetPadding           (ID3Tag *tag, bool pad);
  ID3_C_EXPORT void        ID3Tag_AddFrame             (ID3Tag *tag, const ID3Frame *frame);
  ID3_C_EXPORT void        ID3Tag_AttachFrame          (ID3Tag *tag, ID3Frame *frame);
  ID3_C_EXPORT void        ID3Tag_AddFrames            (ID3Tag *tag, const ID3Frame *frames, size_t num);
  ID3_C_EXPORT ID3Frame   *ID3Tag_RemoveFrame          (ID3Tag *tag, const ID3Frame *frame);
  ID3_C_EXPORT ID3_Err     ID3Tag_Parse                (ID3Tag *tag, const uchar header[ID3_TAGHEADERSIZE], const uchar *buffer);
  ID3_C_EXPORT size_t      ID3Tag_Link                 (ID3Tag *tag, const char *fileName);
  ID3_C_EXPORT size_t      ID3Tag_LinkWithFlags        (ID3Tag *tag, const char *fileName, flags_t flags);
  ID3_C_EXPORT ID3_Err     ID3Tag_Update               (ID3Tag *tag);
  ID3_C_EXPORT ID3_Err     ID3Tag_UpdateByTagType      (ID3Tag *tag, flags_t type);
  ID3_C_EXPORT ID3_Err     ID3Tag_Strip                (ID3Tag *tag, flags_t ulTagFlags);
  ID3_C_EXPORT ID3Frame   *ID3Tag_FindFrameWithID      (const ID3Tag *tag, ID3_FrameID id);
  ID3_C_EXPORT ID3Frame   *ID3Tag_FindFrameWithINT     (const ID3Tag *tag, ID3_FrameID id, ID3_FieldID fld, uint32 data);
  ID3_C_EXPORT ID3Frame   *ID3Tag_FindFrameWithASCII   (const ID3Tag *tag, ID3_FrameID id, ID3_FieldID fld, const char *data);
  ID3_C_EXPORT ID3Frame   *ID3Tag_FindFrameWithUNICODE (const ID3Tag *tag, ID3_FrameID id, ID3_FieldID fld, const unicode_t *data);
  ID3_C_EXPORT size_t      ID3Tag_NumFrames            (const ID3Tag *tag);
  ID3_C_EXPORT bool        ID3Tag_HasTagType           (const ID3Tag *tag, ID3_TagType);
  ID3_C_EXPORT ID3TagIterator* ID3Tag_CreateIterator   (ID3Tag *tag);
  ID3_C_EXPORT ID3TagConstIterator* ID3Tag_CreateConstIterator(const ID3Tag *tag);

  ID3_C_EXPORT void        ID3TagIterator_Delete       (ID3TagIterator*);
  ID3_C_EXPORT ID3Frame   *ID3TagIterator_GetNext      (ID3TagIterator*);
  ID3_C_EXPORT void        ID3TagConstIterator_Delete  (ID3TagConstIterator*);
  ID3_C_EXPORT const ID3Frame *ID3TagConstIterator_GetNext(ID3TagConstIterator*);

  /* frame wrappers */
  ID3_C_EXPORT ID3Frame   *ID3Frame_New                (void);
  ID3_C_EXPORT ID3Frame   *ID3Frame_NewID              (ID3_FrameID id);
  ID3_C_EXPORT void        ID3Frame_Delete             (ID3Frame *frame);
  ID3_C_EXPORT void        ID3Frame_Clear              (ID3Frame *frame);
  ID3_C_EXPORT void        ID3Frame_SetID              (ID3Frame *frame, ID3_FrameID id);
  ID3_C_EXPORT ID3_FrameID ID3Frame_GetID              (const ID3Frame *frame);
  ID3_C_EXPORT ID3Field   *ID3Frame_GetField           (const ID3Frame *frame, ID3_FieldID name);
  ID3_C_EXPORT void        ID3Frame_SetCompression     (ID3Frame *frame, bool comp);
  ID3_C_EXPORT bool        ID3Frame_GetCompression     (const ID3Frame *frame);

  /* field wrappers */
  ID3_C_EXPORT void        ID3Field_Clear              (ID3Field *field);
  ID3_C_EXPORT size_t      ID3Field_Size               (const ID3Field *field);
  ID3_C_EXPORT size_t      ID3Field_GetNumTextItems    (const ID3Field *field);
  ID3_C_EXPORT void        ID3Field_SetINT             (ID3Field *field, uint32 data);
  ID3_C_EXPORT uint32      ID3Field_GetINT             (const ID3Field *field);
  ID3_C_EXPORT void        ID3Field_SetUNICODE         (ID3Field *field, const unicode_t *string);
  ID3_C_EXPORT size_t      ID3Field_GetUNICODE         (const ID3Field *field, unicode_t *buffer, size_t maxChars);
  ID3_C_EXPORT size_t      ID3Field_GetUNICODEItem     (const ID3Field *field, unicode_t *buffer, size_t maxChars, index_t itemNum);
  ID3_C_EXPORT void        ID3Field_AddUNICODE         (ID3Field *field, const unicode_t *string);
  ID3_C_EXPORT void        ID3Field_SetASCII           (ID3Field *field, const char *string);
  ID3_C_EXPORT size_t      ID3Field_GetASCII           (const ID3Field *field, char *buffer, size_t maxChars);
  ID3_C_EXPORT size_t      ID3Field_GetASCIIItem       (const ID3Field *field, char *buffer, size_t maxChars, index_t itemNum);
  ID3_C_EXPORT void        ID3Field_AddASCII           (ID3Field *field, const char *string);
  ID3_C_EXPORT void        ID3Field_SetBINARY          (ID3Field *field, const uchar *data, size_t size);
  ID3_C_EXPORT void        ID3Field_GetBINARY          (const ID3Field *field, uchar *buffer, size_t buffLength);
  ID3_C_EXPORT void        ID3Field_FromFile           (ID3Field *field, const char *fileName);
  ID3_C_EXPORT void        ID3Field_ToFile             (const ID3Field *field, const char *fileName);

  /* field-info wrappers */
  ID3_C_EXPORT char        *ID3FrameInfo_ShortName     (ID3_FrameID frameid);
  ID3_C_EXPORT char        *ID3FrameInfo_LongName      (ID3_FrameID frameid);
  ID3_C_EXPORT const char  *ID3FrameInfo_Description   (ID3_FrameID frameid);
  ID3_C_EXPORT int         ID3FrameInfo_MaxFrameID     (void);
  ID3_C_EXPORT int         ID3FrameInfo_NumFields      (ID3_FrameID frameid);
  ID3_C_EXPORT ID3_FieldType ID3FrameInfo_FieldType    (ID3_FrameID frameid, int fieldnum);
  ID3_C_EXPORT size_t      ID3FrameInfo_FieldSize      (ID3_FrameID frameid, int fieldnum);
  ID3_C_EXPORT flags_t     ID3FrameInfo_FieldFlags     (ID3_FrameID frameid, int fieldnum);

  /* Deprecated */
  ID3_C_EXPORT void        ID3Tag_SetCompression       (ID3Tag *tag, bool comp);

#ifdef __cplusplus
}
#endif /*__cplusplus*/

#endif /* _ID3LIB_ID3_H_ */
