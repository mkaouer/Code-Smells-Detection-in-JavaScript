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

unit CodeCompletion;

interface

uses 
{$IFDEF WIN32}
  Windows, Classes, Forms, SysUtils, Controls, Graphics, CppParser,
  cbutils, IntList;
{$ENDIF}
{$IFDEF LINUX}
  Xlib, Classes, QForms, SysUtils, QControls, QGraphics, CppParser,
  U_IntList, QDialogs, Types;
{$ENDIF}

type
  TCodeCompletion = class(TComponent)
  private
    fParser: TCppParser;
    fFullCompletionStatementList: TList;
    fCompletionStatementList: TList;
    fMinWidth: integer;
    fMinHeight: integer;
    fMaxWidth: integer;
    fMaxHeight: integer;
    fPos: TPoint;
    fColor: TColor;
    fWidth: integer;
    fHeight: integer;
    fEnabled: boolean;
    fShowCount: integer;
    fOnKeyPress: TKeyPressEvent;
    fOnResize: TNotifyEvent;
    fOnlyGlobals: boolean;
    fCurrentStatement: PStatement;
    fHideDelay: integer; // allow empty list once, then hide when empty again
    fIncludedFiles: TStringList;
    fIsIncludedCacheFileName: AnsiString;
    fIsIncludedCacheResult: boolean;
    function ApplyClassFilter(Index, CurrentID: integer; InheritanceIDs: TIntList): boolean;
    function ApplyMemberFilter(Index, CurrentID, ParentID: integer; InheritanceIDs: TIntList): boolean;
    procedure GetCompletionFor(Phrase : AnsiString);
    procedure FilterList(const Member : AnsiString);
    procedure SetPosition(Value: TPoint);
    procedure OnFormResize(Sender: TObject);
    function IsIncluded(const FileName: AnsiString): boolean;
    function IsVisible : boolean;
  public
    constructor Create(AOwner: TComponent); override;
    destructor Destroy; override;
    procedure Search(const Phrase, Filename: AnsiString);
    procedure Hide;
    function SelectedStatement: PStatement;
    property CurrentStatement: PStatement read fCurrentStatement write fCurrentStatement;
  published
    property ShowCount : integer read fShowCount write fShowCount;
    property Parser: TCppParser read fParser write fParser;
    property Position: TPoint read fPos write SetPosition;
    property Color: TColor read fColor write fColor;
    property Width: integer read fWidth write fWidth;
    property Height: integer read fHeight write fHeight;
    property Enabled: boolean read fEnabled write fEnabled;
    property MinWidth: integer read fMinWidth write fMinWidth;
    property MinHeight: integer read fMinHeight write fMinHeight;
    property MaxWidth: integer read fMaxWidth write fMaxWidth;
    property MaxHeight: integer read fMaxHeight write fMaxHeight;
    property OnKeyPress: TKeyPressEvent read fOnKeyPress write fOnKeyPress;
    property OnResize: TNotifyEvent read fOnResize write fOnResize;
    property OnlyGlobals: boolean read fOnlyGlobals write fOnlyGlobals;
    property Visible: boolean read IsVisible;
  end;

procedure Register;

implementation

uses
  CodeCompletionForm, Math;

{ TCodeCompletion }

procedure Register;
begin
  RegisterComponents('Dev-C++', [TCodeCompletion]);
end;

constructor TCodeCompletion.Create(AOwner: TComponent);
begin
  inherited Create(AOwner);

  fIncludedFiles := TStringList.Create;
  fIncludedFiles.Sorted := True;
  fIncludedFiles.Duplicates := dupIgnore;

  fCompletionStatementList := TList.Create;
  fFullCompletionStatementList := TList.Create;

  CodeComplForm := TCodeComplForm.Create(Self);
  CodeComplForm.OnResize := OnFormResize;

  fWidth := 320;
  fHeight := 240;
  fColor := clWindow;
  fEnabled := True;
  fOnlyGlobals := False;
  fShowCount := 100; // keep things fast
  fHideDelay := 0;

  fIsIncludedCacheFileName := '';
  fIsIncludedCacheResult := false;
end;

destructor TCodeCompletion.Destroy;
begin
  FreeAndNil(CodeComplForm);
  FreeAndNil(fCompletionStatementList);
  FreeAndNil(fFullCompletionStatementList);
  FreeAndNil(fIncludedFiles);
  inherited Destroy;
end;

function TCodeCompletion.ApplyClassFilter(Index, CurrentID: integer; InheritanceIDs: TIntList): boolean;
begin
  Result :=
    (
    (PStatement(fParser.Statements[Index])^._Scope in [ssLocal, ssGlobal]) or // local or global var or
    (
    (PStatement(fParser.Statements[Index])^._Scope = ssClassLocal) and // class var
    (
    (PStatement(fParser.Statements[Index])^._ParentID = CurrentID) or // from current class
    (
    (InheritanceIDs.IndexOf(PStatement(fParser.Statements[Index])^._ParentID) <> -1) and
    (PStatement(fParser.Statements[Index])^._ClassScope <> scsPrivate)
    ) // or an inheriting class
    )
    )
    ) and
    (IsIncluded(PStatement(fParser.Statements[Index])^._FileName) or
     IsIncluded(PStatement(fParser.Statements[Index])^._DeclImplFileName));
end;

function TCodeCompletion.ApplyMemberFilter(Index, CurrentID, ParentID: integer; InheritanceIDs: TIntList): boolean;
var
  cs: set of TStatementClassScope;
begin
  Result := PStatement(fParser.Statements[Index])^._ParentID <> -1; // only members
  if not Result then Exit;

  // all members of current class
  Result := Result and ((ParentID = CurrentID) and (PStatement(fParser.Statements[Index])^._ParentID = CurrentID));

  // all public and published members of var's class
  Result := Result or
    (
    (ParentID = PStatement(fParser.Statements[Index])^._ParentID) and
    (not (PStatement(fParser.Statements[Index])^._ClassScope in [scsProtected, scsPrivate])) // or member of an inherited class
    );

  if (CurrentID = -1) or (PStatement(fParser.Statements[Index])^._ParentID = CurrentID) then
    cs := [scsPrivate, scsProtected]
  else
    cs := [scsPrivate];

  // all inherited class's non-private members
  Result := Result or
    (
    (InheritanceIDs.IndexOf(PStatement(fParser.Statements[Index])^._ParentID) <> -1) and
    (not (PStatement(fParser.Statements[Index])^._ClassScope in cs)) // or member of an inherited class
    );
end;

procedure TCodeCompletion.GetCompletionFor(Phrase : AnsiString);
var
	I: integer;
	InheritanceIDs: TIntList;
	CurrentID, ParentID: integer;
	parent : PStatement;
begin
	// Reset filter cache
	fIsIncludedCacheFileName := '';
	fIsIncludedCacheResult := false;

	// Pulling off the same trick as in TCppParser.FindStatementOf, but ignore everything after last operator
	InheritanceIDs := TIntList.Create;
	try
		// ID of current class
		if Assigned(fCurrentStatement) then
			CurrentID := fCurrentStatement^._ID
		else
			CurrentID := -1;

		I := fParser.FindLastOperator(Phrase);
		if I = 0 then begin

			// only add globals and members of the current class

			// Also consider classes the current class inherits from
			fParser.GetInheritanceIDs(fCurrentStatement,InheritanceIDs);
			for I := 0 to fParser.Statements.Count - 1 do
				if ApplyClassFilter(I, CurrentID, InheritanceIDs) then
					fFullCompletionStatementList.Add(fParser.Statements[I]);

		end else begin

			// Find last operator
			Delete(Phrase,I,MaxInt);

			// Add statements of all the text before the last operator
			parent := fParser.FindStatementOf(Phrase,fCurrentStatement);
			if not Assigned(parent) then
				Exit;

			// Then determine which type it has (so we can use it as a parent ID)
			if not (parent^._Kind = skClass) then begin // already found type
				parent := fParser.FindTypeStatementOf(parent^._Type,fCurrentStatement);
				if not Assigned(parent) then
					Exit;
			end;

			ParentID := parent^._ID;
			fParser.GetInheritanceIDs(parent,InheritanceIDs); // slow...

			// Then add members of the ClassIDs and InheritanceIDs
			for I := 0 to fParser.Statements.Count - 1 do
				if ApplyMemberFilter(I, CurrentID, ParentID, InheritanceIDs) then
					fFullCompletionStatementList.Add(fParser.Statements[I]);
		end;
	finally
		InheritanceIDs.Free;
	end;
end;

function ListSort(Item1, Item2: Pointer): Integer;
begin
	// first take into account that parsed statements need to be higher
	// in the list than loaded ones
	if PStatement(Item1)^._Loaded and (not PStatement(Item2)^._Loaded) then
		Result := 1
	else if (not PStatement(Item1)^._Loaded) and PStatement(Item2)^._Loaded then
		Result := -1
	else // otherwise, sort by name
		Result := CompareText(PStatement(Item1)^._ScopelessCmd, PStatement(Item2)^._ScopelessCmd);
end;

procedure TCodeCompletion.FilterList(const Member : AnsiString);
var
	I: integer;
begin
	fCompletionStatementList.Clear;
	if Member <> '' then begin // filter, case insensitive
		fCompletionStatementList.Capacity := fFullCompletionStatementList.Count;
		for I := 0 to fFullCompletionStatementList.Count - 1 do
			if StartsText(Member, PStatement(fFullCompletionStatementList[I])^._ScopelessCmd) then
				fCompletionStatementList.Add(fFullCompletionStatementList[I]);
	end else
		fCompletionStatementList.Assign(fFullCompletionStatementList);
	fCompletionStatementList.Sort(@ListSort);
end;

procedure TCodeCompletion.Hide;
begin
	OnKeyPress := nil;
	CodeComplForm.Hide;

	// Clear data, do not free pointed memory: data is owned by CppParser
	fCompletionStatementList.Clear;
	fFullCompletionStatementList.Clear;
	CodeComplForm.lbCompletion.Items.BeginUpdate;
	CodeComplForm.lbCompletion.Items.Clear;
	CodeComplForm.lbCompletion.Items.EndUpdate;
	fIncludedFiles.Clear; // is recreated anyway on reshow, so save some memory when hiding
end;

procedure TCodeCompletion.Search(const Phrase, Filename: AnsiString);
var
	I : integer;
begin
	if fEnabled then begin

		Screen.Cursor := crHourglass;

		// only perform full new search if just invoked
		if not CodeComplForm.Showing then begin
			fParser.GetFileIncludes(Filename,fIncludedFiles);
			GetCompletionFor(Phrase);
		end;

		// Sort here by member
		I := fParser.FindLastOperator(Phrase);
		while (I > 0) and (I <= Length(Phrase)) and (Phrase[i] in ['.',':','-','>']) do
			Inc(I);

		// filter fFullCompletionStatementList to fCompletionStatementList
		FilterList(Copy(Phrase,I,MaxInt));

		if fCompletionStatementList.Count > 0 then begin
			CodeComplForm.lbCompletion.Items.BeginUpdate;
			CodeComplForm.lbCompletion.Items.Clear;

			// Only slow one hundred statements...
			for I := 0 to min(fShowCount,fCompletionStatementList.Count - 1) do
				CodeComplForm.lbCompletion.Items.AddObject('',fCompletionStatementList[I]);

			CodeComplForm.lbCompletion.Items.EndUpdate;

			CodeComplForm.Show;
			CodeComplForm.lbCompletion.SetFocus;
			if CodeComplForm.lbCompletion.Items.Count > 0 then
				CodeComplForm.lbCompletion.ItemIndex := 0;

			fHideDelay := 0;
		end else if fHideDelay = 0 then begin
			CodeComplForm.lbCompletion.Items.Clear;
			fHideDelay := 1;
		end else begin
			Hide;
		end;

		Screen.Cursor := crDefault;
	end;
end;

function TCodeCompletion.SelectedStatement: PStatement;
begin
  if fEnabled then begin
    if (fCompletionStatementList.Count > CodeComplForm.lbCompletion.ItemIndex) and (CodeComplForm.lbCompletion.ItemIndex <> -1) then
      Result := PStatement(fCompletionStatementList[CodeComplForm.lbCompletion.ItemIndex])
    else begin
      if fCompletionStatementList.Count > 0 then
        Result := PStatement(fCompletionStatementList[0])
      else
        Result := nil;
    end;
  end
  else
    Result := nil;
end;

procedure TCodeCompletion.SetPosition(Value: TPoint);
begin
  fPos := Value;
  if fPos.X + fWidth > Screen.Width then
    CodeComplForm.Left := fPos.X - fWidth
  else
    CodeComplForm.Left := fPos.X;
  if fPos.Y + fHeight > Screen.Height then
    CodeComplForm.Top := fPos.Y - fHeight - 16
  else
    CodeComplForm.Top := fPos.Y;
end;

procedure TCodeCompletion.OnFormResize(Sender: TObject);
begin
  if Enabled then begin
    fWidth := CodeComplForm.Width;
    fHeight := CodeComplForm.Height;
    if Assigned(fOnResize) then
      fOnResize(Self);
  end;
end;

function TCodeCompletion.IsIncluded(const FileName: AnsiString): boolean;
begin
	// Only do the slow check if the cache is invalid
	if not SameStr(FileName,fIsIncludedCacheFileName) then begin
		fIsIncludedCacheFileName := FileName;
		fIsIncludedCacheResult := FastIndexOf(fIncludedFiles,FileName) <> -1;
	end;

	// Cache has been updated. Use it.
	Result := fIsIncludedCacheResult;
end;

function TCodeCompletion.IsVisible : boolean;
begin
	Result := fEnabled and CodeComplForm.Visible;
end;

end.
