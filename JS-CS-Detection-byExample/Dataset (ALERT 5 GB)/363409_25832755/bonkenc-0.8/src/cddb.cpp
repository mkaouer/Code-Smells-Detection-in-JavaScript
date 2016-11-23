 /* BonkEnc version 0.8
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  * Portions Copyright (C) 1999-2002 Albert L. Faber
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#include <smoothx.h>
#include <iolib/drivers/driver_socket.h>
#include <iolib/drivers/driver_socks4.h>
#include <iolib/drivers/driver_socks5.h>
#include <cddb.h>
#include <dllinterfaces.h>

int cddb_sum(int n)
{
	int	 ret = 0;

	while (n > 0)
	{
		ret = ret + (n % 10);
		n = n / 10;
	}

	return ret;
}

bonkEncCDDB::bonkEncCDDB(bonkEncConfig *iConfig)
{
	activeDriveID = 0;
	connected = SMOOTH::False;

	config = iConfig;
}

bonkEncCDDB::~bonkEncCDDB()
{
}

SMOOTHInt bonkEncCDDB::SetActiveDrive(SMOOTHInt driveID)
{
	SMOOTHString	 inifile = SMOOTH::StartDirectory;

	inifile.Append("BonkEnc.ini");

	ex_CR_Init(inifile);

	if (driveID >= ex_CR_GetNumCDROM())
	{
		ex_CR_DeInit();

		return SMOOTH::Error;
	}
	else
	{
		activeDriveID = driveID;

		ex_CR_DeInit();

		return SMOOTH::Success;
	}
}

SMOOTHInt bonkEncCDDB::ComputeDiscID()
{
	SMOOTHString	 inifile = SMOOTH::StartDirectory;

	inifile.Append("BonkEnc.ini");

	ex_CR_Init(inifile);

	ex_CR_SetActiveCDROM(activeDriveID);

	ex_CR_ReadToc();

	SMOOTHInt		 numTocEntries = ex_CR_GetNumTocEntries();
	SMOOTHArray<int>	 tocmin;
	SMOOTHArray<int>	 tocsec;

	for (int j = 0; j <= numTocEntries; j++)
	{
		TOCENTRY	 entry = ex_CR_GetTocEntry(j);

		entry.dwStartSector += 2 * 75;

		tocmin.AddEntry(entry.dwStartSector / 4500);
		tocsec.AddEntry(entry.dwStartSector % 4500 / 75);
	}

	int	 i = 0;
	int	 t = 0;
	int	 n = 0;

	while (i < numTocEntries)
	{
		n = n + cddb_sum((tocmin.GetNthEntry(i) * 60) + tocsec.GetNthEntry(i));

		i++;
	}

	t = ((tocmin.GetLastEntry() * 60) + tocsec.GetLastEntry()) -
	    ((tocmin.GetFirstEntry() * 60) + tocsec.GetFirstEntry());

	ex_CR_DeInit();

	return ((n % 0xff) << 24 | t << 8 | numTocEntries);
}

SMOOTHString bonkEncCDDB::GetDiscIDString()
{
	int	 id = ComputeDiscID();
	SString	 str;

	for (int i = 28; i >= 0; i -= 4)
	{
		if (((id >> i) & 15) <= 9)	str[(28 - i) / 4] = '0' + ((id >> i) & 15);
		else				str[(28 - i) / 4] = 'a' + ((id >> i) & 15) - 10;
	}

	return str;
}

SMOOTHString bonkEncCDDB::GetCDDBQueryString()
{
	SString	 str = SMOOTHString("cddb query ").Append(GetDiscIDString());

	SMOOTHString	 inifile = SMOOTH::StartDirectory;

	inifile.Append("BonkEnc.ini");

	ex_CR_Init(inifile);

	ex_CR_SetActiveCDROM(activeDriveID);

	ex_CR_ReadToc();

	SMOOTHInt	 numTocEntries = ex_CR_GetNumTocEntries();
	TOCENTRY	 entry;

	str.Append(" ").Append(SMOOTHString::IntToString(numTocEntries));

	for (int i = 0; i < numTocEntries; i++)
	{
		entry = ex_CR_GetTocEntry(i);

		str.Append(" ").Append(SMOOTHString::IntToString(entry.dwStartSector + 2 * 75));
	}

	entry = ex_CR_GetTocEntry(numTocEntries);

	str.Append(" ").Append(SMOOTHString::IntToString(entry.dwStartSector / 75 + 2));

	ex_CR_DeInit();

	return str;
}

SMOOTHString bonkEncCDDB::SendCommand(SMOOTHString command)
{
	if (!connected && config->freedb_mode == FREEDB_MODE_CDDBP) return "error not connected";

	SMOOTHString	 str;

#ifdef LOG_CDDB
	OutStream	*log = new OutStream(STREAM_FILE, "cddb.log");
#endif

	switch (config->freedb_mode)
	{
		case FREEDB_MODE_CDDBP:
			if (command != "")
			{
#ifdef LOG_CDDB
				log->OutputString("> ");
				log->OutputLine(command);
#endif

				out->OutputLine(command);
			}

			do
			{
				str = in->InputLine();

#ifdef LOG_CDDB
				log->OutputString("< ");
				log->OutputLine(str);
#endif
			}
			while (str[0] != '2' && str[0] != '3' && str[0] != '4' && str[0] != '5');

			break;
		case FREEDB_MODE_HTTP:
			if (connected)
			{
				delete out;
				delete in;
				delete socket;

				connected = false;
			}

			if (command[0] == 'p' && command[1] == 'r' && command[2] == 'o' && command[3] == 't' && command[4] == 'o')	break;
			if (command[5] == 'h' && command[6] == 'e' && command[7] == 'l' && command[8] == 'l' && command[9] == 'o')	break;
			if (command[0] == 'q' && command[1] == 'u' && command[2] == 'i' && command[3] == 't')				break;
			if (command == "")												break;

			char	*buffer = new char [256];

			gethostname(buffer, 256);

			str.Append("POST ").Append(config->freedb_query_path).Append(" HTTP/1.0\n");
			str.Append("User-Email: ").Append(config->freedb_email).Append("\n");
			str.Append("Content-Length: ").Append(SMOOTHString::IntToString(SMOOTHString("cmd=").Append(command).Append("&hello=user+").Append(buffer).Append("+BonkEnc+v0.8&proto=5\n").Length())).Append("\n");
			str.Append("Charset: ISO-8859-1\n");
			str.Append("\n");

			for (int i = 0; i < command.Length(); i++) if (command[i] == ' ') command[i] = '+';

			str.Append("cmd=").Append(command).Append("&hello=user+").Append(buffer).Append("+BonkEnc+v0.8&proto=5\n");

			delete [] buffer;

			if (config->freedb_proxy_mode == 0)		socket = new IOLibDriverSocket(config->freedb_server, config->freedb_http_port);
			else if (config->freedb_proxy_mode == 1)	socket = new IOLibDriverSOCKS4(config->freedb_proxy, config->freedb_proxy_port, config->freedb_server, config->freedb_http_port);
			else if (config->freedb_proxy_mode == 2)	socket = new IOLibDriverSOCKS5(config->freedb_proxy, config->freedb_proxy_port, config->freedb_server, config->freedb_http_port);

			if (socket->GetLastError() != IOLIB_ERROR_OK)
			{
#ifdef LOG_CDDB
				log->OutputLine(SMOOTHString("Error connecting to CDDB server at ").Append(config->freedb_server).Append(":").Append(SMOOTHString::IntToString(config->freedb_cddbp_port)));
#endif

				str = "error";

				delete socket;

				break;
			}

			in = new InStream(STREAM_DRIVER, socket);
			out = new OutStream(STREAM_STREAM, in);

#ifdef LOG_CDDB
			log->OutputString("\n");
			log->OutputString(str);
			log->OutputString("\n");
#endif

			out->OutputString(str);

			do
			{
				str = in->InputLine();

#ifdef LOG_CDDB
				log->OutputString("< ");
				log->OutputLine(str);
#endif
			}
			while (str != "");

			do
			{
				str = in->InputLine();

#ifdef LOG_CDDB
				log->OutputString("< ");
				log->OutputLine(str);
#endif
			}
			while (str[0] != '2' && str[0] != '3' && str[0] != '4' && str[0] != '5');

			if (str[1] == '1')
			{
				connected = true;
			}
			else
			{
				delete out;
				delete in;
				delete socket;
			}

			break;
	}

#ifdef LOG_CDDB
	delete log;
#endif

	return str;
}

SMOOTHBool bonkEncCDDB::ConnectToServer()
{
	if (config->freedb_mode == FREEDB_MODE_CDDBP)
	{
		if (config->freedb_proxy_mode == 0)		socket = new IOLibDriverSocket(config->freedb_server, config->freedb_cddbp_port);
		else if (config->freedb_proxy_mode == 1)	socket = new IOLibDriverSOCKS4(config->freedb_proxy, config->freedb_proxy_port, config->freedb_server, config->freedb_cddbp_port);
		else if (config->freedb_proxy_mode == 2)	socket = new IOLibDriverSOCKS5(config->freedb_proxy, config->freedb_proxy_port, config->freedb_server, config->freedb_cddbp_port);

		if (socket->GetLastError() != IOLIB_ERROR_OK)
		{
#ifdef LOG_CDDB
			OutStream	*log = new OutStream(STREAM_FILE, "cddb.log");

			log->OutputLine(SMOOTHString("Error connecting to CDDB server at ").Append(config->freedb_server).Append(":").Append(SMOOTHString::IntToString(config->freedb_cddbp_port)));

			delete log;
#endif

			connected = false;

			delete socket;

			return false;
		}

#ifdef LOG_CDDB
		OutStream	*log = new OutStream(STREAM_FILE, "cddb.log");

		log->OutputLine(SMOOTHString("Connected to CDDB server at ").Append(config->freedb_server).Append(":").Append(config->freedb_cddbp_port));

		delete log;
#endif

		connected = true;

		in = new InStream(STREAM_DRIVER, socket);
		out = new OutStream(STREAM_STREAM, in);
	}

	SendCommand("");
	SendCommand("proto 5");

	char	*buffer = new char [256];

	gethostname(buffer, 256);

	SendCommand(SMOOTHString("cddb hello user ").Append(buffer).Append(" BonkEnc v0.8"));

	delete [] buffer;

	return true;
}

SMOOTHString bonkEncCDDB::Query(SMOOTHString discid)
{
	SMOOTHString	 str = SendCommand(GetCDDBQueryString());

	// no match found
	if (str[0] == '2' && str[1] == '0' && str[2] == '2') return "none";

	// exact match
	if (str[0] == '2' && str[1] == '0' && str[2] == '0')
	{
		SMOOTHString	 ret;

		for (int s = 4; s < 256; s++)
		{
			if (str[s] == ' ')
			{
				for (int i = 0; i < 8; i++) ret[s - 4 + i + 1] = str[s + i + 1];

				break;
			}
			else
			{
				ret[s - 4] = str[s];
			}
		}

		return ret;
	}

	// multiple exact matches
	if (str[0] == '2' && str[1] == '1' && (str[2] == '0' || str[2] == '1'))
	{
		ids.DeleteAll();
		titles.DeleteAll();
		categories.DeleteAll();

		do
		{
			SMOOTHString	 val = in->InputLine();
			SMOOTHString	 id;
			SMOOTHString	 title;
			SMOOTHString	 category;

#ifdef LOG_CDDB
			OutStream	*log = new OutStream(STREAM_FILE, "cddb.log");

			log->OutputString("< ");
			log->OutputLine(val);

			delete log;
#endif

			if (val == ".") break;

			for (int s = 0; s < 256; s++)
			{
				if (val[s] == ' ')
				{
					for (int i = 0; i < 8; i++)				id[i] = val[s + i + 1];
					for (int j = 0; j < (val.Length() - s - 10); j++)	title[j] = val[s + j + 10];

					break;
				}
				else
				{
					category[s] = val[s];
				}
			}

			ids.AddEntry(id);
			titles.AddEntry(title);
			categories.AddEntry(category);
		}
		while (true);

		if (str[2] == '0')	return "multiple";
		else			return "fuzzy";
	}

	return "error";
}

SMOOTHString bonkEncCDDB::Read(SMOOTHString query)
{
	SMOOTHString	 str = SendCommand(SMOOTHString("cddb read ").Append(query));

	if (str[0] == '2' && str[1] == '1' && str[2] == '0')
	{
		str = "";

		do
		{
			SMOOTHString	 val = in->InputLine();

#ifdef LOG_CDDB
			OutStream	*log = new OutStream(STREAM_FILE, "cddb.log");

			log->OutputString("< ");
			log->OutputLine(val);

			delete log;
#endif

			if (val == ".") break;

			str.Append(val).Append("\n");
		}
		while (true);

		return str;
	}
	else
	{
		return "error";
	}
}

SMOOTHBool bonkEncCDDB::CloseConnection()
{
	if (!connected && config->freedb_mode == FREEDB_MODE_CDDBP) return false;

	SendCommand("quit");

	if (config->freedb_mode == FREEDB_MODE_CDDBP)
	{
		delete out;
		delete in;
		delete socket;
	}

	return true;
}

SMOOTHInt bonkEncCDDB::GetNOfMatches()
{
	return ids.GetNOfEntries();
}

SMOOTHString bonkEncCDDB::GetNthID(SMOOTHInt n)
{
	return ids.GetNthEntry(n);
}

SMOOTHString bonkEncCDDB::GetNthTitle(SMOOTHInt n)
{
	return titles.GetNthEntry(n);
}

SMOOTHString bonkEncCDDB::GetNthCategory(SMOOTHInt n)
{
	return categories.GetNthEntry(n);
}
