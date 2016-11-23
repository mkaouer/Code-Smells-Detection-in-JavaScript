/* (c)Copyright 1996-2000 NTT Cyber Space Laboratories */
/*                Modified on 2000.09.06 by N. Iwakami */

// Chunk1.h: CChunk �N���X�̃C���^�[�t�F�C�X
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_CHUNK1_H__E059C7BF_6C3B_11D4_A71A_00C04F012175__INCLUDED_)
#define AFX_CHUNK1_H__E059C7BF_6C3B_11D4_A71A_00C04F012175__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <string>
#include <vector>

//////////////////////////////////////
//////////////////////////////////////
// CChunk �N���X�̃C���^�t�F�[�X
// CChunk �N���X�̓`�����N�̊�{�\����񋟂���B���̃f�[�^����o�͂���
class CChunk
{
public:
	typedef std::vector<char> CChunkData;

private:
	std::string m_id;
	CChunkData  m_data;
	CChunkData::iterator m_iter;

protected:
	int   GetNInt( int size=sizeof(long) );			// ���݈ʒu���� size �o�C�g�������擾�i�f�t�H���g�Flong�����j
	CChunkData GetVector( int size = 0 );			// ���݈ʒu���� size �o�C�g���̃x�N�g�����擾�i�f�t�H���g�F�c��S���j
	std::string GetRndString( int length = 0 );		// ���݈ʒu���� length �����̕�������擾�i�f�t�H���g�F�c��S���j

	void PutNInt( unsigned int inputData, int size=sizeof(long) );	// ���݈ʒu���� size �o�C�g�������������݁i�f�t�H���g�Flong�����j

	void  cnk_rewind();								// ���݈ʒu��擪�ɖ߂�
	void cnk_delete();								// �f�[�^����������
	const int   GetCurrentPosition()				// ���݈ʒu���擾����
	{
		return m_iter - m_data.begin();
	};

	// ��O
	class err_FailPut { };

public:
	CChunk( std::string ID );	// �R���X�g���N�^
	virtual ~CChunk();			// �f�X�g���N�^

	// �擾�n�����o�֐�
	const std::string& GetID() { return m_id; };		// ID �̎擾
	const int GetSize() { return m_data.size(); };	// �`�����N�T�C�Y�̎擾
	const CChunkData& GetData() { return m_data; };

	// �������݌n�����o�֐�
	int PutData( int size, char inputData[] );		// �f�[�^��ǉ��A�L�����N�^�z��^
	int PutData( CChunkData& inputData );			// �f�[�^��ǉ��A�x�N�g���^
	int PutData( std::string& theString );			// �f�[�^��ǉ��A������^

	virtual std::string whatami() { return "Raw"; };

};


// CChunkChunk �N���X�̃C���^�[�t�F�C�X
// ������݂̂��i�[����`�����N�A�ėp�`�����N�^
//
//////////////////////////////////////////////////////////////////////
class CStringChunk : public CChunk  
{
public:
	std::string GetString() { cnk_rewind(); return GetRndString(); };
	CStringChunk( const CChunk& parent ) : CChunk( parent ) { };
	CStringChunk( std::string ID, std::string data="" ) : CChunk( ID ) { if ( data != "" ) PutData( data ); };
	virtual ~CStringChunk();

	std::string whatami() { return "String"; };
};


// CIntChunk �e���v���[�g
// �������P�����i�[����`�����N�^�̃e���v���[�g�A�ėp�`�����N�^
//
//////////////////////////////////////////////////////////////////////
template <class TINT>
class CIntChunk : public CChunk  
{
	int m_dataSize;
public:
	const TINT GetInt() { cnk_rewind(); return GetNInt(m_dataSize); };	// �����f�[�^���擾����
	int PutInt( TINT data ) { cnk_delete(); PutNInt( data, m_dataSize ); };

	CIntChunk( const CChunk& parent ) : CChunk(parent) { m_dataSize = sizeof(TINT); };
	CIntChunk( std::string id, TINT data ) : CChunk(id) { 
		m_dataSize = sizeof(TINT);
		PutNInt( data, m_dataSize );
	}
	virtual ~CIntChunk() { };

	std::string whatami() { char retstr[20]; sprintf( retstr, "Integer, size=%d", m_dataSize ); return retstr; };
};


// CChunkChunk �N���X�̃C���^�[�t�F�C�X
// �`�����N���i�[����`�����N�A�ėp�`�����N�^
//
//////////////////////////////////////////////////////////////////////
class CChunkChunk : public CChunk  
{
public:
	CChunk* GetNextChunk( int idSize = 4 );	// ���̃`�����N��Ԃ�
	void rewind() { cnk_rewind(); };		// �����߂�
	void PutChunk( CChunk& src );

	CChunkChunk( const CChunk& parent ) : CChunk( parent ) { };	// �R���X�g���N�^�ECChunk �I�u�W�F�N�g����ɂ���
	CChunkChunk( std::string ID ) : CChunk( ID ) { };		// �R���X�g���N�^�EID �����^����̃`�����N���쐬����
	virtual ~CChunkChunk();									// �f�X�g���N�^

	// ��O
	class err_FailGetChunk { };		// �`�����N�擾�Ɏ��s
	class err_FailPutChunk { };		// �`�����N�������݂Ɏ��s

	std::string whatami() { return "Chunk"; };
};

// CCommChunk �N���X�̃C���^�[�t�F�C�X
//
//////////////////////////////////////////////////////////////////////
class CCommChunk : public CChunk  
{
public:
	typedef std::string string;
	typedef unsigned long CommData;

private:
	string m_version;
	CommData m_channelMode;
	CommData m_bitRate;
	CommData m_samplingRate;
	CommData m_securityLevel;

public:
	CCommChunk( const CChunk& parent, string version="TWIN97012000" );
	CCommChunk( CommData channelMode, CommData bitRate, CommData samplingRate, CommData securityLevel, string version="TWIN97012000" );
	virtual ~CCommChunk();

	CommData GetChannelMode()  { return m_channelMode;  };		// �`���l�����[�h���擾
	CommData GetBitRate()      { return m_bitRate;      };		// �r�b�g���[�g���擾
	CommData GetSamplingRate() { return m_samplingRate; };		// �T���v�����O���g�����擾
	CommData GetSecurityLevel()  { return m_securityLevel;  };	// �ǉ������擾

	// ��O
	class err_FailConstruction { };							// �G���[�F�R���X�g���N�V�����Ɏ��s

	std::string whatami() { return "COMM"; };
};


// CYearChunk �N���X�̃C���^�[�t�F�C�X
//
//////////////////////////////////////////////////////////////////////
class CYearChunk : public CChunk  
{
	short m_year;
	char  m_month;

public:
	const short GetYear() { return m_year; };
	const char GetMonth() { return m_month; };

	CYearChunk( std::string id, short year, char month ) : CChunk( id ) {
		m_year = year;
		m_month = month;
		PutNInt ( m_year, sizeof(short) );
		PutNInt ( m_month, sizeof(char) );
	};
	CYearChunk( const CChunk& parent ) : CChunk( parent ) {
		cnk_rewind();
		m_year = GetNInt( sizeof(short) );
		m_month = GetNInt( sizeof(char) );
	};
	virtual ~CYearChunk() { };

	std::string whatami() { return "YEAR"; };
};

// CEncdChunk �N���X�̃C���^�[�t�F�C�X
//
//////////////////////////////////////////////////////////////////////
class CEncdChunk : public CChunk  
{
	short m_year;
	char  m_month;
	char  m_day;
	char  m_hour;
	char  m_minute;
	char  m_timeZone;

public:
	const short GetYear()     { return m_year; };
	const char  GetMonth()    { return m_month; };
	const char  GetDay()      { return m_day; };
	const char  GetHour()     { return m_hour; };
	const char  GetMinute()   { return m_minute; };
	const char  GetTimeZone() { return m_timeZone; };

	CEncdChunk( std::string id, short year, char month, char day, char hour, char minute, char timeZone );
	CEncdChunk( const CChunk& parent );
	virtual ~CEncdChunk();

	std::string whatami() { return "ENCD"; };
};



/////////////////////////////////////////////////////////
// �ėp�`�����N�^�𗘗p����T�u�`�����N�^�̐錾
/////////////////////////////////////////////////////////
// TWIN �`�����N
typedef CChunkChunk			CTwinChunk;	// TWIN

// �W���`�����N
										// COMM �͔ėp�`�����N�ł͂Ȃ�
typedef CStringChunk		CNameChunk;	// NAME
typedef CStringChunk		CComtChunk;	// COMT
typedef CStringChunk		CAuthChunk;	// AUTH
typedef CStringChunk		CCpyrChunk;	// (c)
typedef CStringChunk		CFileChunk;	// FILE
typedef CIntChunk<unsigned long>	CDsizChunk;	// DSIZ
typedef CChunk				CExtrChunk;	// EXTR

// �g���`�����N�E�ʏ�
typedef CStringChunk		CAlbmChunk;	// ALBM
										// YEAR �͔ėp�`�����N�ł͂Ȃ�
										// ENCD �͔ėp�`�����N�ł͂Ȃ�
typedef CIntChunk<short>	CTracChunk;	// TRAC
typedef CStringChunk		CLyrcChunk;	// LYRC
typedef CChunk				CGuidChunk;	// GUID
typedef	CStringChunk		CIsrcChunk;	// ISRC
typedef	CStringChunk		CWordChunk;	// WORD
typedef CStringChunk		CMuscChunk;	// MUSC
typedef CStringChunk		CArngChunk;	// ARNG
typedef CStringChunk		CProdChunk;	// PROD
typedef CStringChunk		CRemxChunk;	// REMX
typedef CStringChunk		CCdctChunk;	// CDCT
typedef CStringChunk		CSingChunk;	// SING
typedef CStringChunk		CBandChunk;	// BAND
typedef CStringChunk		CPrsnChunk;	// PRSN
typedef CStringChunk		CLablChunk;	// LABL
typedef CStringChunk		CNoteChunk;	// NOTE

// �g���`�����N�E�⏕
typedef CChunkChunk			CScndChunk;	// SCND

// �\��`�����N
typedef CChunk				C_Id3Chunk;	// _ID3
typedef CChunk				C_YmhChunk;	// _YMH
typedef CChunk				C_NttChunk;	// _NTT


#endif // !defined(AFX_CHUNK1_H__E059C7BF_6C3B_11D4_A71A_00C04F012175__INCLUDED_)
