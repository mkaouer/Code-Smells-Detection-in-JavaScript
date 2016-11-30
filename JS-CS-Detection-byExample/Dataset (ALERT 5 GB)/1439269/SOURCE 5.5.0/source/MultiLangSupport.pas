{
    This file is part of Dev-C++
    Copyright (c) 2004 Bloodshed Software

    Dev-C++ is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Dev-C++ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Dev-C++; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
}

(*
 Changes: 12.5.1
 Programmer: Mike Berg
 Desc:  Modified to be a singleton pattern.  Any time the object is called
        through the Lang function the object is auto-created if need be
        and returned. works like devcfg.
 ToUse: The Strings prop is default so just call like Lang[ID_value];
          - it works like a AnsiString list.
*)

unit MultiLangSupport;

interface

uses
{$IFDEF WIN32}
  Windows, Dialogs, SysUtils, Classes;
{$ENDIF}
{$IFDEF LINUX}
  QDialogs, SysUtils, Classes;
{$ENDIF}

{$I LangIDs.inc}

type
  TdevMultiLangSupport = class(TObject)
  private
    fLangList : TStringList;
    fLangFile : AnsiString;
    fCurLang : AnsiString;
    fStrings : TStringList;
    fDefaultLang: TStringList;
    function GetString(ID: integer): AnsiString;
    function GetLangName: AnsiString;
    constructor Create;
  public
    destructor Destroy; override;
    class function Lang: TdevMultiLangSupport;

    procedure CheckLanguageFiles;
    procedure SelectLanguage;

    function Open(const FileName: AnsiString): boolean;
    procedure SetLang(const Lang: AnsiString);

    function FileFromDescription(const Desc: AnsiString): AnsiString;

    property Strings[index: integer]: AnsiString read GetString; default;
    property CurrentLanguage: AnsiString read GetLangName;
    property Langs: TStringList read fLangList write fLangList;
  end;

function Lang: TdevMultiLangSupport;

implementation

uses
{$IFDEF WIN32}
  LangFrm, Forms, utils, version, Controls, devcfg;
{$ENDIF}
{$IFDEF LINUX}
  LangFrm, QForms, utils, version, QControls, devcfg;
{$ENDIF}

var
 fLang: TdevMultiLangSupport = nil;
 fExternal: boolean = true;

function Lang: TdevMultiLangSupport;
begin
  if not assigned(fLang) and not DontRecreateSingletons then
   begin
     fExternal:= false;
     try
      fLang:= TdevMultiLangSupport.Create;
     finally
      fExternal:= true;
     end;
   end;
  result:= fLang;
end;

class function TdevMultiLangSupport.Lang: TdevMultiLangSupport;
begin
  result:= MultiLangSupport.Lang;
end;

constructor TdevMultiLangSupport.Create;
var
	ms: TMemoryStream;
begin
	inherited;
	fLangList:= TStringList.Create;
	fStrings:= TStringList.Create;
	fDefaultLang := TStringList.Create;
	ms:= TMemoryStream.Create;
	try
		LoadFilefromResource('English.lng', ms);
		fStrings.LoadFromStream(ms);
		ms.Seek(0, soFromBeginning);
		fDefaultLang.LoadFromStream(ms);
	finally
		ms.free;
	end;

	CheckLanguageFiles;
end;

destructor TdevMultiLangSupport.Destroy;
begin
	fLangList.Free;
	fStrings.Free;
	fDefaultLang.Free;
	fLang:= nil;
	inherited;
end;

function TdevMultiLangSupport.Open(const Filename : AnsiString): boolean;
var
	s,aFile: AnsiString;
	ver: Integer;
	NewStrs: TStringList;
begin
	result:= false;
	aFile:= ValidateFile(FileName, devDirs.Lang);
	if (aFile = '') then begin
		MessageDlg('Could not open language file ' + filename, mtError, [mbOK], 0);
		exit;
	end;

	try // handle overall errors
		NewStrs:= TStringList.Create;

		try
			NewStrs.LoadFromFile(aFile);
			s:= NewStrs.Values['Ver'];

			// handle invalid version entry
			ver := StrToIntDef(s,-1);
			if ver = -1 then begin
				if MessageDlg('The selected language file has an invalid, or is missing a version entry.'#13#10
						+'You may not have all the required strings for your current Dev-C++ interface.'#13#10
						+'Please check the Dev-C++ Update or Bloodshed.net for new language files, Continue Opening?',
						mtWarning, [mbYes, mbNo], 0) = mrNo then
					Exit
				else
					ver:= 1;
			end;

			fLangFile:= aFile;
			fStrings.Clear;
			fStrings.AddStrings(NewStrs);
		finally
			NewStrs.Free;
		end;

		if ver>=1 then result:= true;

		fCurLang:= fStrings.Values['Lang'];
		if fCurLang = '' then
			fCurLang:= ChangeFileExt(ExtractFileName(aFile), '');
		devData.Language:= ExtractFileName(aFile);
	except
		result:= false;
	end;
end;

procedure TdevMultiLangSupport.CheckLanguageFiles;
var
	idx: integer;
	s: AnsiString;
	tmp: TStringList;
begin
	fLangList.Clear;
	if devDirs.Lang = '' then
		exit;

	FilesFromWildcard(devDirs.Lang , '*.lng',fLangList, False,   False, True);
	fLangList.Sort;
	if fLangList.Count> 0 then begin
		tmp:= TStringList.Create;
		try
			for idx:= 0 to pred(fLangList.Count) do begin
				tmp.LoadFromFile(fLangList[idx]);
				s := tmp.Values['Lang'];
				if SameText(ExtractFileName(fLangList[idx]),'English.lng') and (devData.Language='') then
					fCurLang:=s;
				if s =  '' then
					fLangList[idx]:= format('%s=%s', [fLangList[idx], ChangeFileExt(ExtractFileName(fLangList[idx]), '')])
				else
					fLangList[idx]:= format('%s=%s', [fLangList[idx], s]);
			end;
		finally
			tmp.Free;
		end;
	end;
	if fCurLang='' then
		fCurLang:=devData.Language;
end;

function TdevMultiLangSupport.GetString(ID : integer) : AnsiString;
begin
	result:= fStrings.Values[inttostr(ID)];
	if Result = '' then begin
		Result := fDefaultLang.Values[inttostr(ID)];
	end;
	if result = '' then
		result:= format('<ID %d translation missing>', [ID])
	else
		result:=StringReplace(result, '<CR>', #13#10, [rfReplaceAll]);
end;

function TdevMultiLangSupport.GetLangName: AnsiString;
begin
	result:= fCurLang;
end;

procedure TdevMultiLangSupport.SelectLanguage;
begin
	if fLangList.Count > 0 then begin
		with TLangForm.Create(Application.Mainform) do try
			UpdateList(fLangList);
			ShowModal;
		finally
			Free;
		end;
	end;
end;

procedure TdevMultiLangSupport.SetLang(const Lang: AnsiString);
var
	idx: integer;
begin
	if SameText(Lang,fCurLang) then exit;
	for idx := 0 to fLangList.Count - 1 do
		if SameText(ExtractFileName(fLangList.Names[idx]), Lang) then begin
			Open(fLangList.Names[idx]);
			break;
		end;
end;

function TdevMultiLangSupport.FileFromDescription(const Desc: AnsiString): AnsiString;
var
	i: integer;
begin
	// returns the filename of the lang file described as Desc
	// for example with Desc="English (Original)", returns "English.lng"
	Result:=Desc;
	for i := 0 to fLangList.Count - 1 do
		if SameText(fLangList.ValueFromIndex[i], Desc) then begin
			Result:=ExtractFilename(fLangList.Names[i]);
			Break;
		end;
end;

end.
