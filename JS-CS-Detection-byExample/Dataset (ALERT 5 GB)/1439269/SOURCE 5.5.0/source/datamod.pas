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

unit datamod;

interface

uses
{$IFDEF WIN32}
  SysUtils, Classes, Menus, Controls, SynEditHighlighter, SynHighlighterCpp,
  CodeInsList, SynHighlighterRC, ImgList;
{$ENDIF}
{$IFDEF LINUX}
  SysUtils, Classes, QMenus, QDialogs, QImgList, QControls,
  QSynEditExport, QSynExportHTML, QSynExportRTF,
  QSynEditHighlighter, QSynHighlighterCpp, QSynEditPrint,
  CodeIns, QSynHighlighterRC, QSynCompletionProposal,
  QSynEditMiscClasses, QSynEditSearch;
{$ENDIF}

type
  PMRUItem = ^TMRUItem;
  TMRUItem = record
    filename : AnsiString;
    MenuItem : TMenuItem;
  end;

  TdmMain = class(TDataModule)
    Cpp: TSynCppSyn;
    ProjectImage_Gnome: TImageList;
    MenuImages_Gnome: TImageList;
    Res: TSynRCSyn;
    MenuImages_NewLook: TImageList;
    ProjectImage_NewLook: TImageList;
    GutterImages: TImageList;
    MenuImages_Blue: TImageList;
    ProjectImage_Blue: TImageList;
    ClassImages: TImageList;
    procedure DataModuleCreate(Sender: TObject);
    procedure DataModuleDestroy(Sender: TObject);
  private
    fUnitCount: integer;
    fProjectCount: integer;
    fCodeList: TCodeInsList;
    fCodeMenu: TMenuItem;
    fCodePop: TMenuItem;
    fCodeEvent: TNotifyEvent;
    fCodeOffset: byte;
    procedure LoadCodeIns;
  public
    property CodeMenu: TMenuItem read fCodeMenu write fCodeMenu;
    property CodePop: TMenuItem read fCodePop write fCodePop;
    property CodeClick: TNotifyEvent read fCodeEvent write fCodeEvent;
    property CodeInserts: TCodeInsList read fCodeList write fCodeList;
    property CodeOffset: byte read fCodeOffset write fCodeOffset;

{ MRU List }
   private
    fMRU: TList; // let them store their own location
    fMRUMenu: TMenuItem; // start of list
    fMRUClick: TNotifyEvent;
    procedure FilterHistory; // remove deleted
    procedure LoadHistory;
    procedure SaveHistory;
  public
    procedure RebuildMRU;
    procedure AddtoHistory(const s: AnsiString);
    procedure RemoveFromHistory(const s: AnsiString);
    procedure ClearHistory;
    property MRUMenu: TMenuItem read fMRUMenu write fMRUMenu;
    property MRUClick: TNotifyEvent read fMRUClick write fMRUClick;
    property MRU: TList read fMRU;
  public
    procedure LoadDataMod;
    function GetNewProjectNumber: integer;
    function GetNewFileNumber: integer;
    procedure InitHighlighterFirstTime(index : integer);
    procedure UpdateHighlighter;
    function GetHighlighter(const FileName: AnsiString): TSynCustomHighlighter;
  end;

var
  dmMain: TdmMain;

implementation

uses 
  devcfg, utils, version, math, MultiLangSupport;

{$R *.dfm}

{ TdmMain }

procedure TdmMain.DataModuleCreate(Sender: TObject);
begin
	fMRU:= TList.Create;
	fCodeList:= TCodeInsList.Create;
end;

procedure TdmMain.DataModuleDestroy(Sender: TObject);
var
	I : integer;
begin
	SaveHistory;
	for I := 0 to fMRU.Count - 1 do
		Dispose(PMRUItem(fMRU[i]));
	fMRU.Free;
	fCodeList.Free;
end;

procedure TdmMain.InitHighlighterFirstTime(index : integer);
  procedure AddSpecial(AttrName: AnsiString; Offset: integer);
  var
    a: integer;
  begin
    a:= devEditor.Syntax.IndexofName(AttrName);
    if a = -1 then
      devEditor.Syntax.Append(format('%s=%s', [AttrName, LoadStr(offset)]))
    else
      devEditor.Syntax.Values[AttrName]:= LoadStr(offset);
  end;
var
  i, a, offset : integer;
  Attr: TSynHighlighterAttributes;
begin
  offset := index * 1000;
  for i:= 0 to pred(cpp.AttrCount) do
  begin
    attr:= TSynHighlighterAttributes.Create(cpp.Attribute[i].Name);
    try
      StrtoAttr(Attr, LoadStr(i + offset + 1));
      cpp.Attribute[i].Assign(Attr);
      a:= devEditor.Syntax.IndexOfName(cpp.Attribute[i].Name);
      if a = -1 then
        devEditor.Syntax.Append(format('%s=%s', [cpp.Attribute[i].Name, AttrtoStr(Attr)]))
      else
        devEditor.Syntax.Values[cpp.Attribute[i].Name]:= AttrtoStr(Attr);
    finally
      Attr.Free;
    end;
  end;
  AddSpecial(cBP,  offset+17); // breakpoint
  AddSpecial(cErr, offset+18); // error line
  AddSpecial(cABP, offset+19); // active breakpoint
  AddSpecial(cGut, offset+20); // gutter
  AddSpecial(cSel, offset+21); // selected text
  AddSpecial(cFld, offset+22); // fold bar lines
end;

procedure TdmMain.UpdateHighlighter;
var
 Attr: TSynHighlighterAttributes;
 aName: AnsiString;
 a,
 idx: integer;
begin
  for idx:= 0 to pred(cpp.AttrCount) do
   begin
     aName:= cpp.Attribute[idx].Name;
     a:= devEditor.Syntax.IndexOfName(aName);
     if a <> -1 then
      begin
        Attr:= TSynHighlighterAttributes.Create(aName);
        try
          StrtoAttr(Attr, devEditor.Syntax.Values[aname]);
          cpp.Attribute[idx].Assign(attr);
        finally
          Attr.Free;
        end;
      end;
   end;
  // update res highlighter
  with Res do
   begin
     CommentAttri.Assign(cpp.CommentAttri);
     DirecAttri.Assign(cpp.DirecAttri);
     IdentifierAttri.Assign(cpp.IdentifierAttri);
     KeyAttri.Assign(cpp.KeyAttri);
     NumberAttri.Assign(cpp.NumberAttri);
     SpaceAttri.Assign(cpp.SpaceAttri);
     StringAttri.Assign(cpp.StringAttri);
     SymbolAttri.Assign(cpp.SymbolAttri);
   end;
end;

function TdmMain.GetHighlighter(const FileName: AnsiString): TSynCustomHighlighter;
var
	ext: AnsiString;
	idx: integer;
	tmp: TStrings;
begin
	UpdateHighlighter;
	result:= nil;
	if devEditor.UseSyntax then begin
		if (FileName = '') or (Pos(Lang[ID_UNTITLED], FileName) = 1) then
			result:= cpp
		else begin
			ext:= ExtractFileExt(FileName);
			if CompareText(ext, RC_EXT) = 0 then
				result:= Res
			else begin
				tmp:= TStringList.Create;
				try
					delete(ext, 1, 1);
					tmp.Delimiter:= ';';
					tmp.DelimitedText:= devEditor.SyntaxExt;
					if tmp.Count> 0 then
						for idx:= 0 to pred(tmp.Count) do
							if CompareText(Ext, tmp[idx]) = 0 then begin
								result:= cpp;
								Exit;
							end;
				finally
					tmp.Free;
				end;
			end;
		end;
	end;
end;

function TdmMain.GetNewFileNumber: integer;
begin
	Inc(fUnitCount);
	result := fUnitCount;
end;

function TdmMain.GetNewProjectNumber: integer;
begin
	Inc(fProjectCount);
	result := fProjectCount;
end;

procedure TdmMain.LoadDataMod;
begin
  LoadHistory;
  LoadCodeIns;
  UpdateHighlighter;
end;

 { ---------- MRU ---------- }

procedure TdmMain.AddtoHistory(const s: AnsiString);
var
	I: integer;
	newitem : PMRUItem;
begin
	if (s = '') or not FileExists(s) then
		exit;

	// Don't add duplicates!
	for I := 0 to fMRU.Count - 1 do
		if SameText(s,PMRUItem(fMRU[i])^.filename) then
			Exit;

	newitem := new(PMRUItem);
	newitem^.filename := s;
	newitem^.MenuItem := nil; // to be filled by RebuildMRU

	if GetFileTyp(s) = utPrj then begin
		fMRU.Insert(0, newitem);// insert first
	end else begin // find last project
		I := 0;
		while (i < fMRU.Count) and (GetFileTyp(PMRUItem(fMRU[I])^.filename) = utPrj) do
			Inc(I);
		fMRU.Insert(I, newitem);// insert after last project
	end;

	RebuildMRU;
end;

procedure TdmMain.RemoveFromHistory(const s: AnsiString);
var
	I : integer;
begin

	// Remove one, duplicates simply aren't present
	for I := 0 to fMRU.Count - 1 do
		if SameText(s,PMRUItem(fMRU[i])^.filename) then begin

			// remove menu item now
			PMRUItem(fMRU[i])^.MenuItem.Free;

			// delete pointed memory
			Dispose(PMRUItem(fMRU[i]));
			fMRU.Delete(i);

			// Rebuild whole list
			RebuildMRU;
			break;
		end;
end;

procedure TdmMain.ClearHistory;
var
	I : integer;
begin
	for I := 0 to fMRU.Count - 1 do begin

		// remove menu item now
		PMRUItem(fMRU[i])^.MenuItem.Free;

		// delete pointed memory
		Dispose(PMRUItem(fMRU[i]));
	end;
	fMRU.Clear;
	RebuildMRU;
end;

procedure TdmMain.FilterHistory; // remove deleted files
var
	I: integer;
begin
	for I := MRU.Count - 1 downto 0 do
		if not FileExists(PMRUItem(fMRU[i])^.filename) then begin
			Dispose(PMRUItem(fMRU[i]));
			fMRU.Delete(i);
		end;
end;

procedure TdmMain.LoadHistory;
var
	I,J: integer;
	sl : TStringList;
	newitem : PMRUItem;
begin
	sl := TStringList.Create;
	try

		// Use already open file handle
		devData.ReadStrings('History',sl);

		// Delete files that don't exist anymore
		for I := sl.Count - 1 downto 0 do
			if not FileExists(sl.ValueFromIndex[I]) then
				sl.Delete(I);

		// Remove duplicates
		for I := 0 to sl.Count - 1 do begin
			J := I + 1;
			while J < sl.Count do begin
				if(sl.ValueFromIndex[i] = sl.ValueFromIndex[j]) then
					sl.Delete(j)
				else
					Inc(j);
			end;
		end;

		// Create struct list
		for I := 0 to sl.Count - 1 do begin
			newitem := new(PMRUItem);
			newitem^.filename := sl.ValueFromIndex[i];
			newitem^.MenuItem := nil; // to be filled by RebuildMRU
			fMRU.Add(newitem);
		end;
	finally
		sl.Free;
	end;

	RebuildMRU; // rebuild once
end;

procedure TdmMain.SaveHistory;
var
	I: integer;
	sl : TStringList;
begin
	sl := TStringList.Create;
	try

		// Read struct list
		for I := 0 to fMRU.Count - 1 do
			sl.Add(PMRUItem(fMRU[i])^.filename);

		// Use already open file handle
		devData.WriteStrings('History',sl);
	finally
		sl.Free;
	end;
end;

procedure TdmMain.RebuildMRU;
var
	i,startidx,AllCount,ProjCount,FileCount: integer;
	Parent,Item,TopSep,MiddleSep,BottomSep: TMenuItem;
	fNewMRU: TList;
begin
	// Delete all menu items
	for I:= 0 to fMRU.Count - 1 do
		FreeAndNil(PMRUItem(fMRU[i])^.MenuItem);

	// Remove deleted files
	FilterHistory;

	// Determine where in the main menu to dump our list
	Parent := fMRUMenu.Parent; // Clear history item
	startidx := Parent.IndexOf(fMRUMenu); // start above
	if startidx = -1 then Exit; // no menu item given?

	TopSep := Parent[startidx-3];
	MiddleSep := Parent[startidx-2];
	BottomSep := Parent[startidx-1];
	Dec(startidx,2); // start below the top separator

	// Only save what is currently visible
	fNewMRU := TList.Create;

	// Add menu items up to MRUmax
	AllCount := 0;
	ProjCount := 0;
	FileCount := 0; // other files

	// First add projects
	for I := 0 to min(devData.MRUMax,fMRU.Count) - 1 do begin
		if GetFileTyp(PMRUItem(fMRU[I])^.filename) = utPrj then begin
			Item := TMenuItem.Create(Parent);
			Item.Caption:= Format('&%1x %s', [AllCount, PMRUItem(fMRU[I])^.filename]);
			Item.OnClick:= fMRUClick;
			Item.Tag := I;
			Parent.Insert(startidx + AllCount,Item);

			// Hand a pointer to the MRU item, so it can remove it itself
			PMRUItem(fMRU[I])^.MenuItem := Item;

			fNewMRU.Add(fMRU[I]);
			Inc(AllCount);
			Inc(ProjCount);
			if AllCount = devData.MRUMax then
				break;
		end;
	end;

	// Then add other stuff
	if AllCount <> devData.MRUMax then begin
		for I := 0 to min(devData.MRUMax,fMRU.Count) - 1 do begin
			if GetFileTyp(PMRUItem(fMRU[I])^.filename) <> utPrj then begin
				Item := TMenuItem.Create(Parent);
				Item.Caption:= Format('&%1x %s', [AllCount, PMRUItem(fMRU[I])^.filename]);
				Item.OnClick:= fMRUClick;
				Item.Tag := I;
				Parent.Insert(startidx + AllCount + 1,Item); // add AFTER middle separator

				// Hand a pointer to the MRU item, so it can remove it itself
				PMRUItem(fMRU[I])^.MenuItem := Item;

				fNewMRU.Add(fMRU[I]);
				Inc(AllCount);
				Inc(FileCount);
				if AllCount = devData.MRUMax then
					break;
			end;
		end;
	end;

	// Hide unneeded separators and clear history button
	TopSep.Visible := (AllCount > 0);
	MiddleSep.Visible := (FileCount > 0) and (ProjCount > 0);
	BottomSep.Visible := (AllCount > 0);
	fMRUMenu.Visible := (AllCount > 0);

	// update MRU
	fMRU.Assign(fNewMRU);
	fNewMRU.Free;
end;

{ ---------- Code Insert Methods ---------- }

// Loads code inserts, when sep value changes a separator is
// insert only if sep is a higher value then previous sep value.
procedure TdmMain.LoadCodeIns;
var
 cdx,
 idx: integer;
 Item: TMenuItem;
begin
  if not assigned(fCodeMenu) then exit;
  fCodeList.LoadCode;

  for idx:= pred(fCodeMenu.Count) downto fCodeOffset do
   fCodeMenu[idx].Free;

  if assigned(fCodePop) then
   fCodePop.Clear;

  cdx:= 0;
  for idx:= 0 to pred(fCodeList.Count) do
   begin
     Item:= TMenuItem.Create(fCodeMenu);
     Item.Caption:= fCodeList[idx]^.Caption;
     Item.OnClick:= fCodeEvent;
     Item.Tag:= idx;
     if fCodeList[idx]^.Sep <= cdx then
      fCodeMenu.Add(Item)
     else
      begin
        cdx:= fCodeList[idx]^.Sep;
        fCodeMenu.NewBottomLine;
        fCodeMenu.Add(Item);
      end;
   end;
  fCodeMenu.Visible:= fCodeMenu.Count> 0;
  if assigned(fCodePop) then
   CloneMenu(fCodeMenu, fCodePop);
end;

end.
