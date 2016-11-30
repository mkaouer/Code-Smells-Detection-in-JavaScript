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

unit project;

interface

uses
{$IFDEF WIN32}
  IniFiles, SysUtils, Dialogs, ComCtrls, Editor, Contnrs, SynExportHTML,
  Classes, Controls, version, Forms, Templates, prjtypes,
  Windows;
{$ENDIF}
{$IFDEF LINUX}
IniFiles, SysUtils, QDialogs, QComCtrls, Editor, Contnrs,
Classes, QControls, version, prjtypes, Templates, QForms;
{$ENDIF}

type
  TProjUnit = class;
  TProject = class;

  TUnitList = class
  private
    fList: TList;
    function GetCount: integer;
    function GetItem(index: integer): TProjUnit;
  public
    constructor Create;
    destructor Destroy; override;
    function Add(aunit: TProjUnit): integer;
    procedure Remove(index: integer);
    function IndexOf(const FileName: AnsiString): integer; overload;
    function IndexOf(Editor: TEditor): integer; overload;
    property Items[index: integer]: TProjUnit read GetItem; default;
    property Count: integer read GetCount;
  end;

  TProjUnit = class
  private
    fParent: TProject;
    fEditor: TEditor;
    fFileName: AnsiString;
    fNew: boolean;
    fNode: TTreeNode;
    fFolder: AnsiString;
    fCompile: boolean;
    fCompileCpp: boolean;
    fOverrideBuildCmd: boolean;
    fBuildCmd: AnsiString;
    fLink: boolean;
    fPriority: integer;
    function GetDirty: boolean;
    procedure SetDirty(value: boolean);
    function Save: boolean;
  public
    constructor Create(aOwner: TProject);
    destructor Destroy; override;
    property Editor: TEditor read fEditor write fEditor;
    property FileName: AnsiString read fFileName write fFileName;
    property New: boolean read fNew write fNew;
    property Dirty: boolean read GetDirty write SetDirty;
    property Node: TTreeNode read fNode write fNode;
    property Parent: TProject read fParent write fParent;
    property Folder: AnsiString read fFolder write fFolder;
    property Compile: boolean read fCompile write fCompile;
    property CompileCpp: boolean read fCompileCpp write fCompileCpp;
    property OverrideBuildCmd: boolean read fOverrideBuildCmd write fOverrideBuildCmd;
    property BuildCmd: AnsiString read fBuildCmd write fBuildCmd;
    property Link: boolean read fLink write fLink;
    property Priority: integer read fPriority write fPriority;
    procedure Assign(Source: TProjUnit);
  end;

  TProject = class
  private
    fUnits: TUnitList;
    fOptions: TProjOptions;
    finiFile: TMemIniFile;
    fFileName: AnsiString;
    fName: AnsiString;
    fNode: TTreeNode;
    fModified: boolean;
    fFolders: TStringList;
    fFolderNodes: TObjectList;
    function GetDirectory: AnsiString;
    function GetExecutableName: AnsiString;
    procedure SetFileName(const value: AnsiString);
    function GetModified: boolean;
    procedure SetModified(value: boolean);
    procedure SortUnitsByPriority;
  public
    property Options: TProjOptions read fOptions write fOptions;
    property Name: AnsiString read fName write fName;
    property FileName: AnsiString read fFileName write SetFileName;
    property Node: TTreeNode read fNode write fNode;
    property Directory: AnsiString read GetDirectory;
    property Executable: AnsiString read GetExecutableName;
    property Units: TUnitList read fUnits write fUnits;
    property INIFile: TMemIniFile read fINIFile write fINIFile;
    property Modified: boolean read GetModified write SetModified;
    constructor Create(const nFileName, nName: AnsiString);
    destructor Destroy; override;
    function NewUnit(NewProject: boolean; Folder: TTreeNode; const CustomFileName: AnsiString = ''): integer;
    function AddUnit(s: AnsiString; var pFolder: TTreeNode; Rebuild: Boolean): TProjUnit;
    function GetFolderPath(Node: TTreeNode): AnsiString;
    procedure UpdateFolders;
    procedure AddFolder(const s: AnsiString);
    function OpenUnit(index: integer): TEditor;
    procedure CloseUnit(index: integer);
    procedure SaveUnitAs(i: integer; sFileName: AnsiString); // save single [UnitX]
    procedure SaveAll; // save [Project] and  all [UnitX]
    procedure LoadLayout; // load all [UnitX]
    procedure LoadUnitLayout(e: TEditor; Index: integer); // load single [UnitX] cursor positions
    procedure SaveLayout; // save all [UnitX]
    procedure SaveUnitLayout(e: TEditor; Index: integer); // save single [UnitX] cursor positions
    function MakeProjectNode: TTreeNode;
    function MakeNewFileNode(const s: AnsiString; IsFolder: boolean; NewParent: TTreeNode): TTreeNode;
    procedure BuildPrivateResource(ForceSave: boolean = False);
    procedure LoadOptions;
    procedure SaveOptions;
    function SaveUnits: Boolean;
    procedure Open;
    function FileAlreadyExists(const s: AnsiString): boolean;
    function Remove(index: integer; DoClose: boolean): boolean;
    function GetUnitFromEditor(ed: TEditor): integer;
    function GetUnitFromString(const s: AnsiString): integer;
    procedure RebuildNodes;
    function ListUnitStr(Separator: char): AnsiString;
    procedure ExportToHTML;
    procedure ShowOptions;
    function AssignTemplate(const aFileName: AnsiString; aTemplate: TTemplate): boolean;
    function FolderNodeFromName(const name: AnsiString): TTreeNode;
    procedure CreateFolderNodes;
    procedure UpdateNodeIndexes;
    procedure SetNodeValue(value: TTreeNode);
    procedure CheckProjectFileForUpdate;
    procedure IncrementBuildNumber;
    function GetCompilerOption(const OptionString: AnsiString): Char;
    procedure SetCompilerOption(const OptionString: AnsiString; Value: Char);
    procedure SaveToLog;
  end;

implementation

uses
  main, MultiLangSupport, devcfg, ProjectOptionsFrm, datamod, utils,
  RemoveUnitFrm, SynEdit;

{ TProjUnit }

constructor TProjUnit.Create(aOwner: TProject);
begin
  inherited Create;
  fEditor := nil;
  fNode := nil;
  fParent := aOwner;
end;

destructor TProjUnit.Destroy;
begin
  if Assigned(fEditor) then
    FreeAndNil(fEditor);
  fNode := nil;
  inherited;
end;

function TProjUnit.Save: boolean;
var
  workeditor: TSynEdit;
begin
  try
    result := true;
    if not Assigned(fEditor) and not FileExists(fFileName) then begin // file is neither open, nor saved
      workeditor := TSynEdit.Create(nil);
      workeditor.UnCollapsedLines.SaveToFile(fFileName);
      workeditor.Free;
    end else if Assigned(fEditor) and fEditor.Text.Modified then begin
      result := fEditor.Save;
      if FileExists(fEditor.FileName) then
        FileSetDate(fEditor.FileName, DateTimeToFileDate(Now));
    end;
  except
    result := false;
  end;

  // Update node text
  if Assigned(fNode) then
    fNode.Text := ExtractFileName(fFileName);
end;

function TProjUnit.GetDirty: boolean;
begin
  if assigned(fEditor) then
    result := fEditor.Text.Modified
  else
    result := FALSE;
end;

procedure TProjUnit.SetDirty(value: boolean);
begin
  if assigned(fEditor) then
    fEditor.Text.Modified := value;
end;

procedure TProjUnit.Assign(Source: TProjUnit);
begin
  fEditor := Source.fEditor;
  fFileName := Source.fFileName;
  fNew := Source.fNew;
  Dirty := Source.Dirty;
  fNode := Source.fNode;
  fParent := Source.fParent;
  fFolder := Source.fFolder;
  fCompile := Source.fCompile;
  fCompileCpp := Source.fCompileCpp;
  fOverrideBuildCmd := Source.fOverrideBuildCmd;
  fBuildCmd := Source.fBuildCmd;
  fLink := Source.fLink;
  fPriority := Source.fPriority;
end;

{ TProject }

constructor TProject.Create(const nFileName, nName: AnsiString);
begin
  inherited Create;
  fNode := nil;
  fFolders := TStringList.Create;
  fFolders.Duplicates := dupIgnore;
  fFolders.Sorted := True;
  fFolderNodes := TObjectList.Create(false);
  fUnits := TUnitList.Create;
  fFileName := nFileName;
  finiFile := TMemIniFile.Create(fFileName);
  fOptions := TProjOptions.Create;
  if nName = DEV_INTERNAL_OPEN then
    Open
  else begin
    fName := nName;
    fIniFile.WriteString('Project', 'filename', nFileName);
    fIniFile.WriteString('Project', 'name', nName);
    fNode := MakeProjectNode;
  end;
end;

destructor TProject.Destroy;
begin
  if fModified then
    SaveAll;
  fFolders.Free;
  fFolderNodes.Free;
  fIniFile.Free;
  fUnits.Free;
  if Assigned(fNode) and (not fNode.Deleting) then
    fNode.Free;
  fOptions.Free;
  inherited;
end;

function TProject.GetCompilerOption(const OptionString: AnsiString): Char;
var
  OptionStruct: PCompilerOption;
  OptionIndex: integer;
begin
  Result := '0';

  // Does the option exist?
  if devCompilerSets[fOptions.CompilerSet].FindOption(OptionString, OptionStruct, OptionIndex) then begin
    // Can it be found in the project options list?
    if (OptionIndex + 1 <= Length(fOptions.CompilerOptions)) then
      result := fOptions.CompilerOptions[OptionIndex + 1];
  end;
end;

procedure TProject.SetCompilerOption(const OptionString: AnsiString; value: char);
var
  OptionStruct: PCompilerOption;
  OptionIndex: integer;
begin
  // Does the option exist?
  if devCompilerSets[fOptions.CompilerSet].FindOption(OptionString, OptionStruct, OptionIndex) then begin
    // Can it be found in the project options list?
    if (OptionIndex + 1 <= Length(fOptions.CompilerOptions)) and (fOptions.CompilerOptions[OptionIndex + 1] <> value) then
      begin
      fOptions.CompilerOptions[OptionIndex + 1] := Value;
      SetModified(true);
    end;
  end;
end;

procedure TProject.SaveToLog;
var
  temp: AnsiString;
  temp2: AnsiString;
  i: integer;
  logfile: TextFile;
begin
  temp := '';

  // The commented line below is used by the project logger
  if Options.LogOutputEnabled then begin

    // Formatted log
    if (MainForm.CompilerOutput.Items.Count > 0) then begin
      AssignFile(logfile, Options.LogOutput + '\Formatted Compiler Output.txt');
      try
        if FileExists(Options.LogOutput + '\Formatted Compiler Output.txt') = false then begin
          Rewrite(logfile);
          Write(logfile, DateTimeToStr(Now) + ': Creating log...' + #13#10#13#10);
        end else begin
          Reset(logfile);
          Append(logfile);
          Write(logfile, #13#10 + DateTimeToStr(Now) + ': Appending to log...' + #13#10#13#10);
        end;

        for i := 0 to pred(MainForm.CompilerOutput.Items.Count) do begin
          temp2 := MainForm.CompilerOutput.Items[i].Caption + #10 + MainForm.CompilerOutput.Items[i].SubItems.Text;
          temp2 := StringReplace(temp2, #10, #9, []);
          temp2 := StringReplace(temp2, #13#10, #9, []);
          temp2 := StringReplace(temp2, #13#10, #9, []);
          temp := temp + temp2;
        end;
        Write(logfile, temp);
      finally
        CloseFile(logfile);
      end;
    end;

    // Raw log
    if Length(MainForm.LogOutput.Text) > 0 then begin
      AssignFile(logfile, Options.LogOutput + '\Raw Build Output.txt');
      try
        if FileExists(Options.LogOutput + '\Raw Build Output.txt') = false then begin
          Rewrite(logfile);
          Write(logfile, DateTimeToStr(Now) + ': Creating log...' + #13#10#13#10);
        end else begin
          Reset(logfile);
          Append(logfile);
          Write(logfile, #13#10 + DateTimeToStr(Now) + ': Appending to log...' + #13#10#13#10);
        end;
        Write(logfile, MainForm.LogOutput.Lines.Text);
      finally
        CloseFile(logfile);
      end;
    end;
  end;
end;

function TProject.MakeProjectNode: TTreeNode;
begin
  MakeProjectNode := MainForm.ProjectView.Items.Add(nil, Name);
  MakeProjectNode.SelectedIndex := 0;
  MakeProjectNode.ImageIndex := 0;
  MainForm.ProjectView.FullExpand;
end;

{ begin XXXKF changed }

function TProject.MakeNewFileNode(const s: AnsiString; IsFolder: boolean; NewParent: TTreeNode): TTreeNode;
begin
  MakeNewFileNode := MainForm.ProjectView.Items.AddChild(NewParent, s);

  if IsFolder then begin
    MakeNewFileNode.SelectedIndex := 4;
    MakeNewFileNode.ImageIndex := 4;
  end else begin
    MakeNewFileNode.SelectedIndex := 1;
    MakeNewFileNode.ImageIndex := 1;
  end;
end;
{ end XXXKF changed }

procedure TProject.BuildPrivateResource(ForceSave: boolean = False);
var
  ResFile, Original: TStringList;
  Res, Def, Icon: AnsiString;
  comp, i: Integer;
begin
  comp := 0;
  for i := 0 to Units.Count - 1 do
    if GetFileTyp(Units[i].FileName) = utResSrc then
      if Units[i].Compile then
        Inc(comp);

  // if project has no other resources included
  // and does not have an icon
  // and does not include the XP style manifest
  // and does not include version info
  // then do not create a private resource file
  if (comp = 0) and (not Options.SupportXPThemes) and (not Options.IncludeVersionInfo) and (Options.Icon = '') then begin
    fOptions.PrivateResource := '';
    exit;
  end;

  // change private resource from <project_filename>.res
  // to <project_filename>_private.res
  //
  // in many cases (like in importing a MSVC project)
  // the project's resource file has already the
  // <project_filename>.res filename.
  if Length(Options.PrivateResource) > 0 then begin
    Res := GetRealPath(Options.PrivateResource, Directory);
    if ChangeFileExt(Res, DEV_EXT) = FileName then
      Res := ChangeFileExt(FileName, '_private' + RC_EXT);
  end else
    Res := ChangeFileExt(FileName, '_private' + RC_EXT);
  Res := StringReplace(ExtractRelativePath(FileName, Res), ' ', '_', [rfReplaceAll]);

  // don't run the private resource file and header if not modified,
  // unless ForceSave is true
  if (not ForceSave) and FileExists(Res) and FileExists(ChangeFileExt(Res, H_EXT)) and not Modified then
    Exit;

  ResFile := TStringList.Create;
  ResFile.Add('/* THIS FILE WILL BE OVERWRITTEN BY DEV-C++ */');
  ResFile.Add('/* DO NOT EDIT! */');
  ResFile.Add('');

  if Options.IncludeVersionInfo then begin
    ResFile.Add('#include <windows.h> // include for version info constants');
    ResFile.Add('');
  end;

  for i := 0 to Units.Count - 1 do
    if GetFileTyp(Units[i].FileName) = utResSrc then
      if Units[i].Compile then
        ResFile.Add('#include "' + GenMakePath(ExtractRelativePath(Directory, Units[i].FileName), False, False) + '"');

  if Length(Options.Icon) > 0 then begin
    ResFile.Add('');
    Icon := GetRealPath(Options.Icon, Directory);
    if FileExists(Icon) then begin
      Icon := ExtractRelativePath(FileName, Icon);
      Icon := StringReplace(Icon, '\', '/', [rfReplaceAll]);
      ResFile.Add('A ICON "' + Icon + '"')
    end else
      fOptions.Icon := '';
  end;

  if fOptions.SupportXPThemes then begin
    ResFile.Add('');
    ResFile.Add('//');
    ResFile.Add('// SUPPORT FOR WINDOWS XP THEMES:');
    ResFile.Add('// THIS WILL MAKE THE PROGRAM USE THE COMMON CONTROLS');
    ResFile.Add('// LIBRARY VERSION 6.0 (IF IT IS AVAILABLE)');
    ResFile.Add('//');
    if (Options.ExeOutput <> '') then
      ResFile.Add('1 24 "' + GenMakePath2(IncludeTrailingPathDelimiter(Options.ExeOutput) + ExtractFileName(Executable))
        + '.Manifest"')
    else
      ResFile.Add('1 24 "' + ExtractFileName(Executable) + '.Manifest"');
  end;

  if Options.IncludeVersionInfo then begin
    ResFile.Add('');
    ResFile.Add('//');
    ResFile.Add('// TO CHANGE VERSION INFORMATION, EDIT PROJECT OPTIONS...');
    ResFile.Add('//');
    ResFile.Add('1 VERSIONINFO');
    ResFile.Add('FILEVERSION ' + Format('%d,%d,%d,%d', [Options.VersionInfo.Major, Options.VersionInfo.Minor,
      Options.VersionInfo.Release, Options.VersionInfo.Build]));
    ResFile.Add('PRODUCTVERSION ' + Format('%d,%d,%d,%d', [Options.VersionInfo.Major, Options.VersionInfo.Minor,
      Options.VersionInfo.Release, Options.VersionInfo.Build]));
    case Options.typ of
      dptGUI,
        dptCon: ResFile.Add('FILETYPE VFT_APP');
      dptStat: ResFile.Add('FILETYPE VFT_STATIC_LIB');
      dptDyn: ResFile.Add('FILETYPE VFT_DLL');
    end;
    ResFile.Add('{');
    ResFile.Add('  BLOCK "StringFileInfo"');
    ResFile.Add('  {');
    ResFile.Add('    BLOCK "' + Format('%4.4x%4.4x', [fOptions.VersionInfo.LanguageID, fOptions.VersionInfo.CharsetID])
      +
      '"');
    ResFile.Add('    {');
    ResFile.Add('      VALUE "CompanyName", "' + fOptions.VersionInfo.CompanyName + '"');
    ResFile.Add('      VALUE "FileVersion", "' + fOptions.VersionInfo.FileVersion + '"');
    ResFile.Add('      VALUE "FileDescription", "' + fOptions.VersionInfo.FileDescription + '"');
    ResFile.Add('      VALUE "InternalName", "' + fOptions.VersionInfo.InternalName + '"');
    ResFile.Add('      VALUE "LegalCopyright", "' + fOptions.VersionInfo.LegalCopyright + '"');
    ResFile.Add('      VALUE "LegalTrademarks", "' + fOptions.VersionInfo.LegalTrademarks + '"');
    ResFile.Add('      VALUE "OriginalFilename", "' + fOptions.VersionInfo.OriginalFilename + '"');
    ResFile.Add('      VALUE "ProductName", "' + fOptions.VersionInfo.ProductName + '"');
    ResFile.Add('      VALUE "ProductVersion", "' + fOptions.VersionInfo.ProductVersion + '"');
    ResFile.Add('    }');
    ResFile.Add('  }');

    // additional block for windows 95->NT
    ResFile.Add('  BLOCK "VarFileInfo"');
    ResFile.Add('  {');
    ResFile.Add('    VALUE "Translation", ' + Format('0x%4.4x, %4.4d', [fOptions.VersionInfo.LanguageID,
      fOptions.VersionInfo.CharsetID]));
    ResFile.Add('  }');

    ResFile.Add('}');
  end;

  Res := GetRealPath(Res, Directory);
  if ResFile.Count > 3 then begin
    if FileExists(Res) and not ForceSave then begin
      Original := TStringList.Create;
      Original.LoadFromFile(Res);
      if CompareStr(Original.Text, ResFile.Text) <> 0 then begin
        ResFile.SaveToFile(Res);
      end;
      Original.Free;
    end else begin
      ResFile.SaveToFile(Res);
    end;
    fOptions.PrivateResource := ExtractRelativePath(Directory, Res);
  end else begin
    if FileExists(Res) then
      DeleteFile(PAnsiChar(Res));
    Res := ChangeFileExt(Res, RES_EXT);
    if FileExists(Res) then
      DeleteFile(PAnsiChar(Res));
    fOptions.PrivateResource := '';
  end;
  if FileExists(Res) then
    FileSetDate(Res, DateTimeToFileDate(Now)); // fix the "Clock skew detected" warning ;)

  // create XP manifest
  if fOptions.SupportXPThemes then begin
    ResFile.Clear;
    ResFile.Add('<?xml version="1.0" encoding="UTF-8" standalone="yes"?>');
    ResFile.Add('<assembly');
    ResFile.Add('  xmlns="urn:schemas-microsoft-com:asm.v1"');
    ResFile.Add('  manifestVersion="1.0">');
    ResFile.Add('<assemblyIdentity');
    ResFile.Add('    name="DevCpp.Apps.' + StringReplace(Name, ' ', '_', [rfReplaceAll]) + '"');
    ResFile.Add('    processorArchitecture="*"');
    ResFile.Add('    version="1.0.0.0"');
    ResFile.Add('    type="win32"/>');
    ResFile.Add('<description>' + Name + '</description>');
    ResFile.Add('<dependency>');
    ResFile.Add('    <dependentAssembly>');
    ResFile.Add('        <assemblyIdentity');
    ResFile.Add('            type="win32"');
    ResFile.Add('            name="Microsoft.Windows.Common-Controls"');
    ResFile.Add('            version="6.0.0.0"');
    ResFile.Add('            processorArchitecture="*"');
    ResFile.Add('            publicKeyToken="6595b64144ccf1df"');
    ResFile.Add('            language="*"');
    ResFile.Add('        />');
    ResFile.Add('    </dependentAssembly>');
    ResFile.Add('</dependency>');
    ResFile.Add('</assembly>');
    ResFile.SaveToFile(Executable + '.Manifest');
    FileSetDate(Executable + '.Manifest', DateTimeToFileDate(Now)); // fix the "Clock skew detected" warning ;)
  end else if FileExists(Executable + '.Manifest') then
    DeleteFile(PAnsiChar(Executable + '.Manifest'));

  // create private header file
  Res := ChangeFileExt(Res, H_EXT);
  ResFile.Clear;
  Def := StringReplace(ExtractFilename(UpperCase(Res)), '.', '_', [rfReplaceAll]);
  ResFile.Add('/* THIS FILE WILL BE OVERWRITTEN BY DEV-C++ */');
  ResFile.Add('/* DO NOT EDIT ! */');
  ResFile.Add('');
  ResFile.Add('#ifndef ' + Def);
  ResFile.Add('#define ' + Def);
  ResFile.Add('');
  ResFile.Add('/* VERSION DEFINITIONS */');
  ResFile.Add('#define VER_STRING'#9 + Format('"%d.%d.%d.%d"', [fOptions.VersionInfo.Major, fOptions.VersionInfo.Minor,
    fOptions.VersionInfo.Release, fOptions.VersionInfo.Build]));
  ResFile.Add('#define VER_MAJOR'#9 + IntToStr(fOptions.VersionInfo.Major));
  ResFile.Add('#define VER_MINOR'#9 + IntToStr(fOptions.VersionInfo.Minor));
  ResFile.Add('#define VER_RELEASE'#9 + IntToStr(fOptions.VersionInfo.Release));
  ResFile.Add('#define VER_BUILD'#9 + IntToStr(fOptions.VersionInfo.Build));
  ResFile.Add('#define COMPANY_NAME'#9'"' + fOptions.VersionInfo.CompanyName + '"');
  ResFile.Add('#define FILE_VERSION'#9'"' + fOptions.VersionInfo.FileVersion + '"');
  ResFile.Add('#define FILE_DESCRIPTION'#9'"' + fOptions.VersionInfo.FileDescription + '"');
  ResFile.Add('#define INTERNAL_NAME'#9'"' + fOptions.VersionInfo.InternalName + '"');
  ResFile.Add('#define LEGAL_COPYRIGHT'#9'"' + fOptions.VersionInfo.LegalCopyright + '"');
  ResFile.Add('#define LEGAL_TRADEMARKS'#9'"' + fOptions.VersionInfo.LegalTrademarks + '"');
  ResFile.Add('#define ORIGINAL_FILENAME'#9'"' + fOptions.VersionInfo.OriginalFilename + '"');
  ResFile.Add('#define PRODUCT_NAME'#9'"' + fOptions.VersionInfo.ProductName + '"');
  ResFile.Add('#define PRODUCT_VERSION'#9'"' + fOptions.VersionInfo.ProductVersion + '"');
  ResFile.Add('');
  ResFile.Add('#endif /*' + Def + '*/');
  ResFile.SaveToFile(Res);

  if FileExists(Res) then
    FileSetDate(Res, DateTimeToFileDate(Now)); // fix the "Clock skew detected" warning ;)

  ResFile.Free;
end;

function TProject.NewUnit(NewProject: boolean; Folder: TTreeNode; const CustomFileName: AnsiString): integer;
var
  newunit: TProjUnit;
  s: AnsiString;
  ParentNode, CurNode: TTreeNode;
begin
  NewUnit := TProjUnit.Create(Self);
  if Folder <> nil then
    ParentNode := Folder
  else
    ParentNode := Node;
  with NewUnit do try
      if Length(CustomFileName) = 0 then
        s := Directory + Lang[ID_Untitled] + inttostr(dmMain.GetNewFileNumber)
      else begin
        if ExtractFilePath(CustomFileName) = '' then // just filename, no path
          // make it full path filename, so that the save dialog, starts at the right directory ;)
          s := Directory + CustomFileName
        else
          s := CustomFileName;
      end;

      if FileAlreadyExists(s) then
        repeat
          s := Directory + Lang[ID_Untitled] + inttostr(dmMain.GetNewFileNumber);
        until not FileAlreadyExists(s);

      Filename := s;
      New := True;
      Editor := nil;
      CurNode := MakeNewFileNode(ExtractFileName(FileName), false, ParentNode);
      NewUnit.Folder := ParentNode.Text;
      NewUnit.Node := CurNode;
      result := fUnits.Add(NewUnit);
      CurNode.Data := pointer(result);
      Dirty := TRUE;
      Compile := True;
      CompileCpp := Self.Options.useGPP;
      Link := True;
      Priority := 1000;
      OverrideBuildCmd := False;
      BuildCmd := '';
      SetModified(TRUE);
    except
      result := -1;
      NewUnit.Free;
    end;
end;

{ begin XXXKF changed }

function TProject.AddUnit(s: AnsiString; var pFolder: TTreeNode; Rebuild: Boolean): TProjUnit;
var
  NewUnit: TProjUnit;
  s2: AnsiString;
  TmpNode: TTreeNode;
begin
  result := nil;
  if s[length(s)] = '.' then // correct filename if the user gives an alone dot to force the no extension
    s[length(s)] := #0;
  NewUnit := TProjUnit.Create(Self);
  with NewUnit do try
      if FileAlreadyExists(s) then begin
        if fname = '' then
          s2 := fFileName
        else
          s2 := fName;
        MessageDlg(format(Lang[ID_MSG_FILEINPROJECT], [s, s2])
          + #13#10 + Lang[ID_SPECIFY], mtError, [mbok], 0);
        NewUnit.Free;
        Exit;
      end;
      FileName := s;
      New := False;
      Editor := nil;
      Node := MakeNewFileNode(ExtractFileName(FileName), false, pFolder);
      Node.Data := pointer(fUnits.Add(NewUnit));
      Folder := pFolder.Text;
      TmpNode := pFolder.Parent;
      while Assigned(TmpNode) and (TmpNode <> self.fNode) do begin
        Folder := TmpNode.Text + '/' + Folder;
        TmpNode := TmpNode.Parent;
      end;

      case GetFileTyp(s) of
        utcSrc, utcppSrc, utcHead, utcppHead: begin
            Compile := True;
            CompileCpp := Self.Options.useGPP;
            Link := True;
          end;
        utResSrc: begin
            Compile := True;
            CompileCpp := Self.Options.useGPP;
            Link := False;
            // if a resource was added, force (re)creation of private resource...
            BuildPrivateResource(True);
          end;
      else begin
          Compile := False;
          CompileCpp := False;
          Link := False;
        end;
      end;
      Priority := 1000;
      OverrideBuildCmd := False;
      BuildCmd := '';
      if Rebuild then
        RebuildNodes;
      SetModified(TRUE);
      Result := NewUnit;
    except
      result := nil;
      NewUnit.Free;
    end;
end;
{ end XXXKF changed }

procedure TProject.LoadOptions;
begin
  with finiFile do begin

    fName := ReadString('Project', 'name', '');
    fOptions.Icon := ReadString('Project', 'icon', '');
    fOptions.Ver := ReadInteger('Project', 'Ver', 0);
    if (fOptions.Ver > 0) then begin // ver > 0 is at least a v5 project

      if (fOptions.Ver < 2) then begin
        fOptions.Ver := 2;
        MessageDlg('The compiler settings format of Orwell Dev-C++ has changed.' + #13#10#13#10 +
          'Please update your settings at Project >> Project Options >> Compiler and save your project.', MtInformation,
          [MbOK], 0);
      end;

      fOptions.typ := ReadInteger('Project', 'type', 0);
      fOptions.CompilerCmd := ReadString('Project', 'Compiler', '');
      fOptions.CppCompilerCmd := ReadString('Project', 'CppCompiler', '');
      fOptions.LinkerCmd := ReadString('Project', 'Linker', '');
      fOptions.ObjFiles.DelimitedText := ReadString('Project', 'ObjFiles', '');
      fOptions.Libs.DelimitedText := ReadString('Project', 'Libs', '');
      fOptions.Includes.DelimitedText := ReadString('Project', 'Includes', '');
      fOptions.PrivateResource := ReadString('Project', 'PrivateResource', '');
      fOptions.ResourceIncludes.DelimitedText := ReadString('Project', 'ResourceIncludes', '');
      fOptions.MakeIncludes.DelimitedText := ReadString('Project', 'MakeIncludes', '');
      fOptions.UseGpp := ReadBool('Project', 'IsCpp', FALSE);
      fOptions.ExeOutput := ReadString('Project', 'ExeOutput', '');
      fOptions.ObjectOutput := ReadString('Project', 'ObjectOutput', '');
      fOptions.LogOutput := ReadString('Project', 'LogOutput', '');
      fOptions.LogOutputEnabled := ReadBool('Project', 'LogOutputEnabled', FALSE);
      fOptions.OverrideOutput := ReadBool('Project', 'OverrideOutput', FALSE);
      fOptions.OverridenOutput := ReadString('Project', 'OverrideOutputName', '');
      fOptions.HostApplication := ReadString('Project', 'HostApplication', '');
      fOptions.UseCustomMakefile := ReadBool('Project', 'UseCustomMakefile', FALSE);
      fOptions.CustomMakefile := ReadString('Project', 'CustomMakefile', '');
      fOptions.CmdLineArgs := ReadString('Project', 'CommandLine', '');
      fFolders.CommaText := ReadString('Project', 'Folders', '');
      fOptions.IncludeVersionInfo := ReadBool('Project', 'IncludeVersionInfo', False);
      fOptions.SupportXPThemes := ReadBool('Project', 'SupportXPThemes', False);
      fOptions.CompilerSet := ReadInteger('Project', 'CompilerSet', devCompilerSets.CurrentIndex);
      if (fOptions.CompilerSet >= devCompilerSets.Count) then begin
        MessageDlg('The compiler set you have selected for this project, no longer exists.'#13#10'It will be substituted by the global compiler set...', mtError, [mbOk], 0);
        fOptions.CompilerSet := devCompilerSets.CurrentIndex; // TODO: translate
      end;
      if Assigned(devCompilerSets.CurrentSet) then
        fOptions.CompilerOptions := ReadString('Project', 'CompilerSettings', devCompilerSets.CurrentSet.OptionString)
      else
        fOptions.CompilerOptions := ReadString('Project', 'CompilerSettings', '');

      fOptions.VersionInfo.Major := ReadInteger('VersionInfo', 'Major', 0);
      fOptions.VersionInfo.Minor := ReadInteger('VersionInfo', 'Minor', 1);
      fOptions.VersionInfo.Release := ReadInteger('VersionInfo', 'Release', 1);
      fOptions.VersionInfo.Build := ReadInteger('VersionInfo', 'Build', 1);
      fOptions.VersionInfo.LanguageID := ReadInteger('VersionInfo', 'LanguageID', $0409);
      fOptions.VersionInfo.CharsetID := ReadInteger('VersionInfo', 'CharsetID', $04E4);
      fOptions.VersionInfo.CompanyName := ReadString('VersionInfo', 'CompanyName', '');
      fOptions.VersionInfo.FileVersion := ReadString('VersionInfo', 'FileVersion', '0.1');
      fOptions.VersionInfo.FileDescription := ReadString('VersionInfo', 'FileDescription',
        'Developed using the Dev-C++ IDE');
      fOptions.VersionInfo.InternalName := ReadString('VersionInfo', 'InternalName', '');
      fOptions.VersionInfo.LegalCopyright := ReadString('VersionInfo', 'LegalCopyright', '');
      fOptions.VersionInfo.LegalTrademarks := ReadString('VersionInfo', 'LegalTrademarks', '');
      fOptions.VersionInfo.OriginalFilename := ReadString('VersionInfo', 'OriginalFilename',
        ExtractFilename(Executable));
      fOptions.VersionInfo.ProductName := ReadString('VersionInfo', 'ProductName', Name);
      fOptions.VersionInfo.ProductVersion := ReadString('VersionInfo', 'ProductVersion', '0.1.1.1');
      fOptions.VersionInfo.AutoIncBuildNr := ReadBool('VersionInfo', 'AutoIncBuildNr', False);
      fOptions.VersionInfo.SyncProduct := ReadBool('VersionInfo', 'SyncProduct', False);
    end else begin // dev-c < 4
      fOptions.Ver := -1;
      if not ReadBool('Project', 'NoConsole', TRUE) then
        fOptions.typ := dptCon
      else if ReadBool('Project', 'IsDLL', FALSE) then
        fOptions.Typ := dptDyn
      else
        fOptions.typ := dptGUI;

      fOptions.PrivateResource := ReadString('Project', 'PrivateResource', '');
      fOptions.ResourceIncludes.DelimitedText := ReadString('Project', 'ResourceIncludes', '');
      fOptions.ObjFiles.Add(ReadString('Project', 'ObjFiles', ''));
      fOptions.Includes.Add(ReadString('Project', 'IncludeDirs', ''));
      fOptions.CompilerCmd := ReadString('Project', 'CompilerOptions', '');
      fOptions.usegpp := ReadBool('Project', 'Use_GPP', FALSE);
      fOptions.ExeOutput := ReadString('Project', 'ExeOutput', '');
      fOptions.ObjectOutput := ReadString('Project', 'ObjectOutput', '');
      fOptions.OverrideOutput := ReadBool('Project', 'OverrideOutput', FALSE);
      fOptions.OverridenOutput := ReadString('Project', 'OverrideOutputName', '');
      fOptions.HostApplication := ReadString('Project', 'HostApplication', '');
    end;
  end;
end;

procedure TProject.SaveOptions;
begin
  with finiFile do begin
    WriteString('Project', 'FileName', ExtractRelativePath(Directory, fFileName));
    WriteString('Project', 'Name', fName);
    WriteInteger('Project', 'Type', fOptions.typ);
    WriteInteger('Project', 'Ver', 2); // Is 2 as of Dev-C++ 5.2.0.3
    WriteString('Project', 'ObjFiles', fOptions.ObjFiles.DelimitedText);
    WriteString('Project', 'Includes', fOptions.Includes.DelimitedText);
    WriteString('Project', 'Libs', fOptions.Libs.DelimitedText);
    WriteString('Project', 'PrivateResource', fOptions.PrivateResource);
    WriteString('Project', 'ResourceIncludes', fOptions.ResourceIncludes.DelimitedText);
    WriteString('Project', 'MakeIncludes', fOptions.MakeIncludes.DelimitedText);
    WriteString('Project', 'Compiler', fOptions.CompilerCmd);
    WriteString('Project', 'CppCompiler', fOptions.CppCompilerCmd);
    WriteString('Project', 'Linker', fOptions.LinkerCmd);
    WriteBool('Project', 'IsCpp', fOptions.UseGpp);
    WriteString('Project', 'Icon', ExtractRelativePath(Directory, fOptions.Icon));
    WriteString('Project', 'ExeOutput', fOptions.ExeOutput);
    WriteString('Project', 'ObjectOutput', fOptions.ObjectOutput);
    WriteString('Project', 'LogOutput', fOptions.LogOutput);
    WriteBool('Project', 'LogOutputEnabled', fOptions.LogOutputEnabled);
    WriteBool('Project', 'OverrideOutput', fOptions.OverrideOutput);
    WriteString('Project', 'OverrideOutputName', fOptions.OverridenOutput);
    WriteString('Project', 'HostApplication', fOptions.HostApplication);
    WriteBool('Project', 'UseCustomMakefile', fOptions.UseCustomMakefile);
    WriteString('Project', 'CustomMakefile', fOptions.CustomMakefile);
    WriteString('Project', 'CommandLine', fOptions.CmdLineArgs);
    WriteString('Project', 'Folders', fFolders.CommaText);
    WriteBool('Project', 'IncludeVersionInfo', fOptions.IncludeVersionInfo);
    WriteBool('Project', 'SupportXPThemes', fOptions.SupportXPThemes);
    WriteInteger('Project', 'CompilerSet', fOptions.CompilerSet);
    WriteString('Project', 'CompilerSettings', fOptions.CompilerOptions);

    WriteInteger('VersionInfo', 'Major', fOptions.VersionInfo.Major);
    WriteInteger('VersionInfo', 'Minor', fOptions.VersionInfo.Minor);
    WriteInteger('VersionInfo', 'Release', fOptions.VersionInfo.Release);
    WriteInteger('VersionInfo', 'Build', fOptions.VersionInfo.Build);
    WriteInteger('VersionInfo', 'LanguageID', fOptions.VersionInfo.LanguageID);
    WriteInteger('VersionInfo', 'CharsetID', fOptions.VersionInfo.CharsetID);
    WriteString('VersionInfo', 'CompanyName', fOptions.VersionInfo.CompanyName);
    WriteString('VersionInfo', 'FileVersion', fOptions.VersionInfo.FileVersion);
    WriteString('VersionInfo', 'FileDescription', fOptions.VersionInfo.FileDescription);
    WriteString('VersionInfo', 'InternalName', fOptions.VersionInfo.InternalName);
    WriteString('VersionInfo', 'LegalCopyright', fOptions.VersionInfo.LegalCopyright);
    WriteString('VersionInfo', 'LegalTrademarks', fOptions.VersionInfo.LegalTrademarks);
    WriteString('VersionInfo', 'OriginalFilename', fOptions.VersionInfo.OriginalFilename);
    WriteString('VersionInfo', 'ProductName', fOptions.VersionInfo.ProductName);
    WriteString('VersionInfo', 'ProductVersion', fOptions.VersionInfo.ProductVersion);
    WriteBool('VersionInfo', 'AutoIncBuildNr', fOptions.VersionInfo.AutoIncBuildNr);
    WriteBool('VersionInfo', 'SyncProduct', fOptions.VersionInfo.SyncProduct);

    if fOptions.Ver <= 0 then begin
      //delete outdated dev4 project options
      DeleteKey('Project', 'NoConsole');
      DeleteKey('Project', 'IsDLL');
      DeleteKey('Project', 'ResFiles');
      DeleteKey('Project', 'IncludeDirs');
      DeleteKey('Project', 'CompilerOptions');
      DeleteKey('Project', 'Use_GPP');
    end;
  end;
end;

function TProject.SaveUnits: Boolean;
var
  Count: integer;
  idx: integer;
  rd_only: boolean;
begin
  Result := False;
  Count := 0;
  rd_only := false;
  for idx := 0 to pred(fUnits.Count) do begin
    with fUnits[idx] do begin

{$WARN SYMBOL_PLATFORM OFF}
      if fUnits[idx].Dirty and FileExists(fUnits[idx].FileName) and (FileGetAttr(fUnits[idx].FileName) and faReadOnly <>
        0) then begin
        // file is read-only
        if MessageDlg(Format(Lang[ID_MSG_FILEISREADONLY], [fUnits[idx].FileName]), mtConfirmation, [mbYes, mbNo], 0) =
          mrNo then
          rd_only := false
        else if FileSetAttr(fUnits[idx].FileName, FileGetAttr(fUnits[idx].FileName) - faReadOnly) <> 0 then begin
          MessageDlg(Format(Lang[ID_MSG_FILEREADONLYERROR], [fUnits[idx].FileName]), mtError, [mbOk], 0);
          rd_only := false;
        end;
      end;
{$WARN SYMBOL_PLATFORM ON}

      if not rd_only and (not fUnits[idx].Save) and New then
        Exit;

      // saved new file or an existing file add to project file
      if not (New and Dirty) then begin
        finifile.WriteString('Unit' + IntToStr(Count + 1), 'FileName', ExtractRelativePath(Directory,
          fUnits[idx].FileName));
        inc(Count);
      end;
      case GetFileTyp(fUnits[idx].FileName) of
        utcHead, utcppHead, utcSrc, utcppSrc: finifile.WriteBool('Unit' + IntToStr(idx + 1), 'CompileCpp', CompileCpp);
        utResSrc: if Folder = '' then
            Folder := 'Resources';
      end;
      finifile.WriteString('Unit' + IntToStr(idx + 1), 'Folder', Folder);
      finifile.WriteBool('Unit' + IntToStr(idx + 1), 'Compile', Compile);
      finifile.WriteBool('Unit' + IntToStr(idx + 1), 'Link', Link);
      finifile.WriteInteger('Unit' + IntToStr(idx + 1), 'Priority', Priority);
      finifile.WriteBool('Unit' + IntToStr(idx + 1), 'OverrideBuildCmd', OverrideBuildCmd);
      finifile.WriteString('Unit' + IntToStr(idx + 1), 'BuildCmd', BuildCmd);
    end;
  end;
  finifile.WriteInteger('Project', 'UnitCount', Count);
  Result := True;
end;

function TProject.FolderNodeFromName(const name: AnsiString): TTreeNode;
var
  i: integer;
begin
  FolderNodeFromName := fNode;
  if name <> '' then
    for i := 0 to pred(fFolders.Count) do begin
      if CompareText(AnsiDequotedStr(fFolders[i], '"'), AnsiDequotedStr(name, '"')) = 0 then begin
        FolderNodeFromName := TTreeNode(fFolderNodes[i]);
        break;
      end;
    end;
end;

procedure TProject.CreateFolderNodes;
var
  idx: integer;
  findnode, node: TTreeNode;
  s: AnsiString;
  I, C: integer;
begin
  fFolderNodes.Clear;
  for idx := 0 to pred(fFolders.Count) do begin
    node := fNode;
    S := fFolders[idx];
    I := Pos('/', S);
    while I > 0 do begin
      findnode := nil;
      for C := 0 to Node.Count - 1 do
        if node.Item[C].Text = Copy(S, 1, I - 1) then begin
          findnode := node.Item[C];
          Break;
        end;
      if not Assigned(findnode) then
        node := MakeNewFileNode(Copy(S, 1, I - 1), True, node)
      else
        node := findnode;
      node.Data := Pointer(-1);
      Delete(S, 1, I);
      I := Pos('/', S);
    end;
    node := MakeNewFileNode(S, True, Node);
    node.Data := Pointer(-1);
    fFolderNodes.Add(node);
  end;
end;

procedure TProject.UpdateNodeIndexes;
var
  idx: integer;
begin
  for idx := 0 to fUnits.Count - 1 do
    fUnits[idx].Node.Data := pointer(idx);
end;

procedure TProject.Open;
var
  ucount,
    i: integer;
  NewUnit: TProjUnit;
begin
{$WARN SYMBOL_PLATFORM OFF}
  if FileExists(FileName) and (FileGetAttr(FileName) and faReadOnly <> 0) then begin
    // file is read-only
    if MessageDlg(Format(Lang[ID_MSG_FILEISREADONLY], [FileName]), mtConfirmation, [mbYes, mbNo], 0) = mrYes then
      if FileSetAttr(FileName, FileGetAttr(FileName) - faReadOnly) <> 0 then begin
        MessageDlg(Format(Lang[ID_MSG_FILEREADONLYERROR], [FileName]), mtError, [mbOk], 0);
      end;
  end;
{$WARN SYMBOL_PLATFORM ON}

  LoadOptions;
  fNode := MakeProjectNode;

  CheckProjectFileForUpdate;

  uCount := fIniFile.ReadInteger('Project', 'UnitCount', 0);
  CreateFolderNodes;
  for i := 0 to pred(uCount) do begin
    NewUnit := TProjUnit.Create(Self);
    with NewUnit do begin
      FileName := ExpandFileto(finifile.ReadString('Unit' + IntToStr(i + 1), 'FileName', ''), Directory);
      if not FileExists(FileName) then begin
        MessageBox(Application.Handle, PAnsiChar(Format(Lang[ID_ERR_FILENOTFOUND], [FileName])), 'Error', MB_ICONERROR);
        SetModified(TRUE);
      end else begin

        Folder := finifile.ReadString('Unit' + IntToStr(i + 1), 'Folder', '');
        Compile := finifile.ReadBool('Unit' + IntToStr(i + 1), 'Compile', True);
        if finifile.ReadInteger('Unit' + IntToStr(i + 1), 'CompileCpp', 2) = 2 then
          // check if feature not present in this file
          CompileCpp := Self.Options.useGPP
        else
          CompileCpp := finifile.ReadBool('Unit' + IntToStr(i + 1), 'CompileCpp', False);
        Link := finifile.ReadBool('Unit' + IntToStr(i + 1), 'Link', True);
        Priority := finifile.ReadInteger('Unit' + IntToStr(i + 1), 'Priority', 1000);
        OverrideBuildCmd := finifile.ReadBool('Unit' + IntToStr(i + 1), 'OverrideBuildCmd', False);
        BuildCmd := finifile.ReadString('Unit' + IntToStr(i + 1), 'BuildCmd', '');

        Editor := nil;
        New := FALSE;
        fParent := self;

        Node := MakeNewFileNode(ExtractFileName(FileName), False, FolderNodeFromName(Folder));
        Node.Data := pointer(fUnits.Add(NewUnit));
      end;
    end;
  end;

  case devData.AutoOpen of
    0: begin
        for i := 0 to pred(fUnits.Count) do
          OpenUnit(i); // Open all
        if fUnits.Count > 0 then
          fUnits[0].Editor.Activate; // Show first
      end;
    1:
      if fUnits.Count > 0 then
        OpenUnit(0).Activate; // Open and show first
    2:
      LoadLayout; // Open previous selection
  end;

  RebuildNodes;
end;

{ end XXXKF changed }

procedure TProject.LoadLayout;
var
  layIni: TIniFile;
  top: integer;
  sl: TStringList;
  idx, currIdx: integer;
begin
  sl := TStringList.Create;
  try
    layIni := TIniFile.Create(ChangeFileExt(Filename, '.layout'));
    try
      top := layIni.ReadInteger('Editors', 'Focused', -1);
      // read order of open files and open them accordingly
      sl.CommaText := layIni.ReadString('Editors', 'Order', '');
    finally
      layIni.Free;
    end;

    for idx := 0 to sl.Count - 1 do begin
      currIdx := StrToIntDef(sl[idx], -1);
      OpenUnit(currIdx);
    end;
  finally
    sl.Free;
  end;

  if (Top <> -1) and (top < fUnits.Count) and Assigned(fUnits[top].Editor) then
    fUnits[top].Editor.Activate;
end;

procedure TProject.LoadUnitLayout(e: TEditor; Index: integer);
var
  layIni: TIniFile;
begin
  layIni := TIniFile.Create(ChangeFileExt(Filename, '.layout'));
  try
    if Assigned(e) then begin
      e.Text.CaretY := layIni.ReadInteger('Editor_' + IntToStr(Index), 'CursorRow', 1);
      e.Text.CaretX := layIni.ReadInteger('Editor_' + IntToStr(Index), 'CursorCol', 1);
      e.Text.TopLine := layIni.ReadInteger('Editor_' + IntToStr(Index), 'TopLine', 1);
      e.Text.LeftChar := layIni.ReadInteger('Editor_' + IntToStr(Index), 'LeftChar', 1);
    end;
  finally
    layIni.Free;
  end;
end;

procedure TProject.SaveLayout;
var
  layIni: TIniFile;
  idx: Integer;
  aset: boolean;
  sl: TStringList;
  S: AnsiString;
begin
  s := ChangeFileExt(Filename, '.layout');
  layIni := TIniFile.Create(s);
  try
    sl := TStringList.Create;
    try
      // write order of open files
      for idx := 0 to MainForm.PageControl.PageCount - 1 do begin
        S := MainForm.PageControl.Pages[idx].Caption;

        // the file is modified and the tabsheet's caption starts with '[*] ' - delete it
        if (Length(S) > 4) and (Copy(S, 1, 4) = '[*] ') then
          S := Copy(S, 5, Length(S) - 4);

        // if this file belongs to the project...
        if sl.IndexOf(IntToStr(fUnits.Indexof(S))) = -1 then
          sl.Add(IntToStr(fUnits.Indexof(S)));
        if MainForm.PageControl.ActivePageIndex = idx then
          layIni.WriteInteger('Editors', 'Focused', fUnits.Indexof(S));
      end;
      layIni.WriteString('Editors', 'Order', sl.CommaText);
    finally
      sl.Free;
    end;

    // save editor info
    for idx := 0 to pred(fUnits.Count) do
      with fUnits[idx] do begin

        // save info on open state
        aset := Assigned(editor);
        layIni.WriteBool('Editor_' + IntToStr(idx), 'Open', aset);
        layIni.WriteBool('Editor_' + IntToStr(idx), 'Top', aset and (Editor.TabSheet =
          Editor.TabSheet.PageControl.ActivePage));
        if aset then begin
          layIni.WriteInteger('Editor_' + IntToStr(idx), 'CursorCol', Editor.Text.CaretX);
          layIni.WriteInteger('Editor_' + IntToStr(idx), 'CursorRow', Editor.Text.CaretY);
          layIni.WriteInteger('Editor_' + IntToStr(idx), 'TopLine', Editor.Text.TopLine);
          layIni.WriteInteger('Editor_' + IntToStr(idx), 'LeftChar', Editor.Text.LeftChar);
        end;

        // remove old data from project file
        fIniFile.DeleteKey('Unit' + IntToStr(idx + 1), 'Open');
        fIniFile.DeleteKey('Unit' + IntToStr(idx + 1), 'Top');
        fIniFile.DeleteKey('Unit' + IntToStr(idx + 1), 'CursorCol');
        fIniFile.DeleteKey('Unit' + IntToStr(idx + 1), 'CursorRow');
        fIniFile.DeleteKey('Unit' + IntToStr(idx + 1), 'TopLine');
        fIniFile.DeleteKey('Unit' + IntToStr(idx + 1), 'LeftChar');
      end;
  finally
    layIni.Free;
  end;
end;

procedure TProject.SaveUnitLayout(e: TEditor; Index: integer);
var
  layIni: TIniFile;
begin
  layIni := TIniFile.Create(ChangeFileExt(Filename, '.layout'));
  try
    if Assigned(e) then begin
      layIni.WriteInteger('Editor_' + IntToStr(Index), 'CursorCol', e.Text.CaretX);
      layIni.WriteInteger('Editor_' + IntToStr(Index), 'CursorRow', e.Text.CaretY);
      layIni.WriteInteger('Editor_' + IntToStr(Index), 'TopLine', e.Text.TopLine);
      layIni.WriteInteger('Editor_' + IntToStr(Index), 'LeftChar', e.Text.LeftChar);
    end;
  finally
    layIni.Free;
  end;
end;

procedure TProject.SaveAll;
begin
  // Save to memory file
  if not SaveUnits then // update [Unitx] list
    Exit;
  SaveOptions; // update other data, and save to disk
  SaveLayout; // save current opened files, and which is "active".

  // Write to disk
  finiFile.UpdateFile;

  // We have saved everything to disk, so mark unmodified
  SetModified(false);
end;

function TProject.Remove(index: integer; DoClose: boolean): boolean;
begin
  result := false;

  // if a resource was removed, force (re)creation of private resource...
  if GetFileTyp(fUnits.GetItem(index).FileName) = utResSrc then
    BuildPrivateResource(True);
  if DoClose and Assigned(fUnits.GetItem(index).fEditor) then begin
    if not MainForm.CloseEditor(fUnits.GetItem(index).fEditor.TabSheet.PageIndex) then
      exit;
  end;

  result := true;

  { this causes problems if the project isn't saved after this, since the erase happens phisically at this moment }
  //if not fUnits.GetItem(index).fNew then
  finifile.EraseSection('Unit' + inttostr(index + 1));
  fUnits.GetItem(index).fNode.Delete;
  fUnits.Remove(index);

  UpdateNodeIndexes;
  SetModified(TRUE);
end;

function TProject.FileAlreadyExists(const s: AnsiString): boolean;
begin
  if fUnits.Indexof(s) > -1 then
    result := true
  else
    result := false;
end;

function TProject.OpenUnit(index: integer): TEditor;
begin
  result := nil;
  if (index < 0) or (index > pred(fUnits.Count)) then
    exit;

  with fUnits[index] do begin
    if FileName <> '' then begin
      try
        SetCurrentDir(Directory);
        fEditor := TEditor.Create(true, ExtractFileName(FileName), ExpandFileName(FileName), not New);
        if New then
          fEditor.InsertDefaultText;
        LoadUnitLayout(fEditor, index);
        result := fEditor;
      except
        MessageDlg(format(Lang[ID_ERR_OPENFILE], [Filename]), mtError, [mbOK], 0);
      end;
    end;
  end;
end;

procedure TProject.CloseUnit(index: integer);
begin
  with fUnits[index] do begin
    if assigned(fEditor) then begin
      SaveUnitLayout(fEditor, index);
      FreeAndNil(fEditor);
    end;
  end;
end;

procedure TProject.SaveUnitAs(i: integer; sFileName: AnsiString);
begin
  if (i < 0) or (i > pred(fUnits.Count)) then
    exit;

  with fUnits[i] do begin
    if FileExists(FileName) then
      New := FALSE;
    FileName := sFileName; // fix bug #559694
    if Editor <> nil then begin
      Editor.UpdateCaption(ExtractFileName(sFileName));
      // project units are referenced with relative paths.
      Editor.FileName := GetRealPath(sFileName, Directory);
    end;
    Node.Text := ExtractFileName(sFileName);
    New := False;
    fInifile.WriteString('Unit' + IntToStr(i + 1), 'FileName', ExtractRelativePath(Directory, sFileName));
  end;
  Modified := true;
end;

function TProject.GetUnitFromEditor(ed: TEditor): integer;
begin
  result := fUnits.Indexof(Ed);
end;

function TProject.GetUnitFromString(const s: AnsiString): integer;
begin
  result := fUnits.Indexof(ExpandFileto(s, Directory));
end;

function TProject.GetExecutableName: AnsiString;
begin
  // Add the proper extension
  if fOptions.OverrideOutput and (fOptions.OverridenOutput <> '') then begin
    result := ExtractFilePath(Filename) + fOptions.OverridenOutput;
  end else begin
    if fOptions.typ = dptStat then
      result := ChangeFileExt(Filename, LIB_EXT)
    else if fOptions.typ = dptDyn then
      result := ChangeFileExt(Filename, DLL_EXT)
    else
      result := ChangeFileExt(Filename, EXE_EXT);
  end;

  // Create alternative directory if possible
  if Length(Options.ExeOutput) > 0 then begin
    if not DirectoryExists(Directory + Options.ExeOutput) then try
        ForceDirectories(Directory + Options.ExeOutput);
      except
        MessageDlg('Could not create executable output directory: "' + Options.ExeOutput +
          '". Please check your access settings.', mtWarning, [mbOK], 0);
        exit;
      end;
    result := GetRealPath(IncludeTrailingPathDelimiter(Directory + Options.ExeOutput) + ExtractFileName(Result));
  end;
end;

function TProject.GetDirectory: AnsiString;
begin
  result := ExtractFilePath(FileName);
end;

function TProject.ListUnitStr(Separator: char): AnsiString;
var
  I: integer;
begin
  Result := '';
  for I := 0 to fUnits.Count - 1 do
    result := result + '"' + ExpandFileName(fUnits[I].FileName) + '"' + Separator;
end;

procedure TProject.SetFileName(const value: AnsiString);
begin
  if fFileName <> value then begin
    fFileName := value;
    SetModified(True);
    finiFile.Rename(value, FALSE);
  end;
end;

function TProject.GetModified: boolean;
var
  I: integer;
  ismod: boolean;
begin
  ismod := FALSE;
  for I := 0 to fUnits.Count - 1 do
    if fUnits[I].Dirty then begin
      ismod := TRUE;
      break;
    end;

  // project file or any unit modified...
  result := fModified or ismod;
end;

procedure TProject.SetModified(value: boolean);
begin
  // only mark modified if *not* read-only
{$WARN SYMBOL_PLATFORM OFF}
  if not FileExists(FileName) or (FileExists(FileName) and (FileGetAttr(FileName) and faReadOnly = 0)) then
{$WARN SYMBOL_PLATFORM ON}
    fModified := value;
end;

procedure TProject.SetNodeValue(value: TTreeNode);
begin
  fNode := Value;
end;

procedure TProject.ExportToHTML;
  function ConvertFilename(const Filename, FinalPath, Extension: AnsiString): AnsiString;
  begin
    Result := ExtractRelativePath(Directory, Filename);
    Result := StringReplace(Result, '.', '_', [rfReplaceAll]);
    Result := StringReplace(Result, '\', '_', [rfReplaceAll]);
    Result := StringReplace(Result, '/', '_', [rfReplaceAll]);
    Result := IncludeTrailingPathDelimiter(FinalPath) + Result + Extension;
  end;
var
  I: integer;
  sl: TStringList;
  fName, UnitName: AnsiString;
  Size: integer;
  SizeStr: AnsiString;
  link: AnsiString;
  BaseDir: AnsiString;
  hFile: integer;
  SynExporterHTML: TSynExporterHTML;
begin
  SynExporterHTML := TSynExporterHTML.Create(nil);
  with TSaveDialog.Create(nil) do try

      Filter := SynExporterHTML.DefaultFilter;
      Title := Lang[ID_NV_EXPORT];
      DefaultExt := HTML_EXT;
      FileName := ChangeFileExt(fFileName, HTML_EXT);
      Options := Options + [ofOverwritePrompt];

      if not Execute then begin
        SynExporterHTML.Free;
        Exit;
      end;

      fName := FileName; // project html filename
    finally
      Free;
    end;

  BaseDir := ExtractFilePath(fName);
  CreateDir(BaseDir + pd + 'files');

  sl := TStringList.Create;
  try
    sl.Add('<html>');
    sl.Add('<head><title>Dev-C++ project: ' + Self.Name + '</title></head>');
    sl.Add('<body bgcolor=#FFFFFF>');
    sl.Add('<h2>Project: ' + Self.Name + '</h2>');
    sl.Add('<b>Index of files:</b>');
    sl.Add('<hr width="80%"/>');
    sl.Add('<table align="center" cellspacing="20">');
    sl.Add('<tr><td><b><u>Filename</u></b></td><td><b><u>Location</u></b></td><td><b><u>Size</u></b></td></tr>');

    // Use this one to export files to HTML (copy from TEditor.ExportToHTML)
    SynExporterHTML.CreateHTMLFragment := False;
    SynExporterHTML.ExportAsText := True;
    SynExporterHTML.UseBackground := True;
    SynExporterHTML.Font := devEditor.Font;

    for I := 0 to Units.Count - 1 do begin
      UnitName := Units[I].FileName;

      hFile := FileOpen(UnitName, fmOpenRead);
      if hFile > 0 then begin
        Size := FileSeek(hFile, 0, 2);
        if Size < 1024 then
          SizeStr := IntToStr(Size) + ' ' + Lang[ID_BYTES]
        else if Size < 1024 * 1024 then
          SizeStr := FloatToStr(Size / 1024) + ' KiB'
        else if Size < 1024 * 1024 * 1024 then
          SizeStr := FloatToStr((Size / 1024) / 1024) + ' MiB'
        else
          SizeStr := FloatToStr(((Size / 1024) / 1024) / 1024) + ' GiB';

        FileClose(hFile);
      end else
        SizeStr := 'Unknown';

      // Add string to main overview
      link := ExtractFilename(ConvertFilename(ExtractRelativePath(Directory, UnitName), BaseDir, HTML_EXT));
      sl.Add(#9 + '<tr><td><a href="files' + pd + link + '">' + ExtractFilename(UnitName) + '</a></td><td>' +
        ExpandFilename(UnitName) + '</td><td>' + SizeStr + '</td></tr>');
    end;

    sl.Add('</table>');
    sl.Add('<hr width="80%"/>');
    sl.Add('<p align="center"><font size="1">Exported by <a href="http://www.bloodshed.net/dev">' + DEVCPP + '</a> v' +
      DEVCPP_VERSION + '</font></p>');
    sl.Add('</body>');
    sl.Add('</html>');
    sl.SaveToFile(fName);

    // export project files
    for I := 0 to Units.Count - 1 do begin
      UnitName := Units[I].FileName;

      // Load from disk
      sl.LoadFromFile(UnitName);

      // Save to disk as HTML
      SynExporterHTML.Title := UnitName;
      SynExporterHTML.Highlighter := dmMain.GetHighlighter(ExtractFilename(UnitName));
      SynExporterHTML.ExportAll(sl);

      UnitName := ConvertFilename(ExtractRelativePath(Directory, UnitName), BaseDir + pd + 'files', HTML_EXT);
      SynExporterHTML.SaveToFile(UnitName);
    end;
  finally
    sl.Free;
    SynExporterHTML.Free;
  end;
end;

procedure TProject.ShowOptions;
var
  IconFileName: AnsiString;
begin
  with TProjectOptionsFrm.Create(MainForm) do try

      // Apply current settings
      SetInterface(Self);

      if ShowModal = mrOk then begin

        // Save new settings to RAM
        GetInterface(Self);

        SetModified(TRUE); // don't save to disk yet
        SortUnitsByPriority;
        RebuildNodes;

        // Copy icon to project directoy
        IconFileName := ChangeFileExt(ExtractFileName(FileName), '.ico');
        if not SameText(IconFileName, fOptions.Icon) and (fOptions.Icon <> '') then begin
          CopyFile(PAnsiChar(fOptions.Icon), PAnsiChar(ExpandFileto(IconFileName, Directory)), False);
          fOptions.Icon := IconFileName;

          // force save of private resource to force rebuild, since icon has changed...
          BuildPrivateResource(True);
        end else
          BuildPrivateResource;

        // update the project's main node caption
        if edProjectName.Text <> '' then begin
          fName := edProjectName.Text;
          fNode.Text := fName;
        end;
      end;

      // Discard changes, even when canceling
      if (cmbCompiler.ItemIndex < devCompilerSets.Count) and (cmbCompiler.ItemIndex >= 0) then
        devCompilerSets.LoadSet(cmbCompiler.ItemIndex);
    finally
      Close;
    end;
end;

function TProject.AssignTemplate(const aFileName: AnsiString; aTemplate: TTemplate): boolean;
var
  I: integer;
  s, s2: AnsiString;
  OriginalIcon, DestIcon: AnsiString;
begin
  result := TRUE;
  try
    if aTemplate.Version = -1 then begin
      fName := format(Lang[ID_NEWPROJECT], [dmmain.GetNewProjectNumber]);
      fNode.Text := fName;
      if Assigned(finiFile) then
        finiFile.Rename(aFileName, false)
      else
        fIniFile := TMemIniFile.Create(aFileName);
      NewUnit(FALSE, nil);
      with fUnits[fUnits.Count - 1] do begin
        Editor := TEditor.Create(true, ExtractFileName(FileName), FileName, false);
        Editor.InsertDefaultText;
        Editor.Activate;
      end;
      exit;
    end;

    fName := aTemplate.Name;
    if Assigned(finiFile) then
      finiFile.Rename(aFileName, false)
    else
      fIniFile := TMemIniFile.Create(aFileName);
    fOptions.Assign(aTemplate.Options);

    // Copy icon to project directory
    if Length(fOptions.Icon) > 0 then begin
      OriginalIcon := ValidateFile(fOptions.Icon, '', true);
      if OriginalIcon <> '' then begin // file found!
        DestIcon := ExpandFileTo(ExtractFileName(ChangeFileExt(FileName, '.ico')), Directory);
        CopyFile(PAnsiChar(OriginalIcon), PAnsiChar(DestIcon), False);
        fOptions.Icon := DestIcon;
      end else
        fOptions.Icon := '';
    end;

    // Add list of files
    if aTemplate.Version > 0 then begin
      for I := 0 to pred(aTemplate.UnitCount) do begin

        // Pick file contents
        if aTemplate.Options.useGPP then
          s := aTemplate.Units[I].CppText
        else
          s := aTemplate.Units[I].CText;

        // Add to project list
        if aTemplate.Options.useGPP then
          NewUnit(FALSE, nil, aTemplate.Units[I].CppName)
        else
          NewUnit(FALSE, nil, aTemplate.Units[I].CName);

        // Create an editor
        with fUnits[fUnits.Count - 1] do begin
          Editor := TEditor.Create(TRUE, ExtractFileName(filename), FileName, FALSE);
          try
            // Set filename depending on C/C++ choice
            if (Length(aTemplate.Units[I].CppName) > 0) and (aTemplate.Options.useGPP) then begin
              Editor.FileName := aTemplate.Units[I].CppName;
              fUnits[fUnits.Count - 1].FileName := aTemplate.Units[I].CppName;
            end else if Length(aTemplate.Units[I].CName) > 0 then begin
              Editor.FileName := aTemplate.Units[I].CName;
              fUnits[fUnits.Count - 1].FileName := aTemplate.Units[I].CName;
            end;

            // if file isn't found blindly inserts text of unit
            s2 := ValidateFile(s, devDirs.Templates);
            if s2 <> '' then begin
              Editor.Text.Lines.LoadFromFile(s2);
            end else if s <> '' then begin
              s := StringReplace(s, '#13#10', #13#10, [rfReplaceAll]);
              Editor.InsertString(s, FALSE);
            end;

            // Always mark modified. We haven't saved yet
            Editor.Text.Modified := TRUE;
            Editor.Activate;
          except
            Editor.Free;
          end;
        end;
      end;

      // Old style...
    end else begin
      NewUnit(FALSE, nil);
      with fUnits[fUnits.Count - 1] do begin
        Editor := TEditor.Create(TRUE, FileName, FileName, FALSE);
        if fOptions.useGPP then
          s := aTemplate.OldData.CppText
        else
          s := aTemplate.OldData.CText;
        s := ValidateFile(s, ExpandFileto(devDirs.Templates, devDirs.Exec));
        if s <> '' then begin
          Editor.Text.Lines.LoadFromFile(s);
          Editor.Text.Modified := TRUE;
        end;
        Editor.Activate;
      end;
    end;
  except
    result := FALSE;
  end;
end;

{ begin XXXKF changed }

procedure TProject.RebuildNodes;
var
  idx: integer;
  oldPaths: TStrings;
  tempnode: TTreeNode;
begin
  MainForm.ProjectView.Items.BeginUpdate;

  //remember if folder nodes were expanded or collapsed
  //create a list of expanded folder nodes
  oldPaths := TStringList.Create;
  with MainForm.ProjectView do
    for idx := 0 to Items.Count - 1 do begin
      tempnode := Items[idx];
      if tempnode.Expanded and (tempnode.Data = Pointer(-1)) then //data=pointer(-1) - it's folder
        oldPaths.Add(GetFolderPath(tempnode));
    end;

  fNode.DeleteChildren;

  CreateFolderNodes;
  {
    for idx:=0 to pred(fFolders.Count) do
      MakeNewFileNode(fFolders[idx], True).Data:=Pointer(-1);}
  for idx := 0 to pred(fUnits.Count) do begin
    fUnits[idx].Node := MakeNewFileNode(ExtractFileName(fUnits[idx].FileName), False,
      FolderNodeFromName(fUnits[idx].Folder));
    fUnits[idx].Node.Data := pointer(idx);
  end;
  for idx := 0 to pred(fFolders.Count) do
    TTreeNode(fFolderNodes[idx]).AlphaSort(False);
  Node.AlphaSort(False);

  //expand nodes expanded before recreating the project tree
  fNode.Collapse(True);
  with MainForm.ProjectView do
    for idx := 0 to Items.Count - 1 do begin
      tempnode := Items[idx];
      if (tempnode.Data = Pointer(-1)) then //it's a folder
        if oldPaths.IndexOf(GetFolderPath(tempnode)) >= 0 then
          tempnode.Expand(False);
    end;
  FreeAndNil(oldPaths);

  fNode.Expand(False);
  MainForm.ProjectView.Items.EndUpdate;
end;
{ end XXXKF changed }

procedure TProject.UpdateFolders;
  procedure RunNode(Node: TTreeNode);
  var
    I: integer;
  begin
    for I := 0 to Node.Count - 1 do
      if Node.Item[I].Data = Pointer(-1) then begin
        fFolders.Add(GetFolderPath(Node.Item[I]));
        if Node.Item[I].HasChildren then
          RunNode(Node.Item[I]);
      end;
  end;
var
  idx: integer;
begin
  fFolders.Clear;
  RunNode(fNode);
  for idx := 0 to Units.Count - 1 do
    Units[idx].Folder := GetFolderPath(Units[idx].Node.Parent);
  SetModified(TRUE);
end;

function TProject.GetFolderPath(Node: TTreeNode): AnsiString;
begin
  Result := '';
  if not Assigned(Node) then
    Exit;

  if Node.Data <> Pointer(-1) then // not a folder
    Exit;

  while Node.Data = Pointer(-1) do begin
    Result := Format('%s/%s', [Node.Text, Result]);
    Node := Node.Parent;
  end;
  Delete(Result, Length(Result), 1); // remove last '/'
end;

procedure TProject.AddFolder(const s: AnsiString);
begin
  if fFolders.IndexOf(s) = -1 then begin
    fFolders.Add(s);
    RebuildNodes;
    MainForm.ProjectView.Select(FolderNodeFromName(s));
    FolderNodeFromName(s).MakeVisible;
    SetModified(TRUE);
  end;
end;

procedure TProject.CheckProjectFileForUpdate;
var
  oldRes: AnsiString;
  sl: TStringList;
  i, uCount: integer;
  cnvt: boolean;
begin
  cnvt := False;
  uCount := fIniFile.ReadInteger('Project', 'UnitCount', 0);

  // check if using old way to store resources and fix it
  oldRes := finifile.ReadString('Project', 'Resources', '');
  if oldRes <> '' then begin
    CopyFile(PAnsiChar(Filename), PAnsiChar(FileName + '.bak'), False);
    sl := TStringList.Create;
    try
      sl.Delimiter := ';';
      sl.DelimitedText := oldRes;
      for i := 0 to sl.Count - 1 do begin
        finifile.WriteString(IntToStr(uCount + i), 'Filename', sl[i]);
        finifile.WriteString(IntToStr(uCount + i), 'Folder', 'Resources');
        finifile.WriteBool(IntToStr(uCount + i), 'Compile', True);
      end;
      fIniFile.WriteInteger('Project', 'UnitCount', uCount + sl.Count);
      oldRes := finifile.ReadString('Project', 'Folders', '');
      if oldRes <> '' then
        oldRes := oldRes + ',Resources'
      else
        oldRes := 'Resources';
      fIniFile.WriteString('Project', 'Folders', oldRes);
      fFolders.Add('Resources');
    finally
      sl.Free;
    end;
    cnvt := True;
  end;

  finifile.DeleteKey('Project', 'Resources');
  finifile.DeleteKey('Project', 'Focused');
  finifile.DeleteKey('Project', 'Order');
  finifile.DeleteKey('Project', 'DebugInfo');
  finifile.DeleteKey('Project', 'ProfileInfo');

  if cnvt then
    MessageDlg('Your project was succesfully updated to a newer file format!'#13#10 +
      'If something has gone wrong, we kept a backup-file: "' +
      FileName + '.bak"...', mtInformation, [mbOk], 0);
end;

procedure TProject.SortUnitsByPriority;
var
  I: integer;
  tmpU: TProjUnit;
  Again: boolean;
begin
  repeat
    I := 0;
    Again := False;
    while I < Units.Count - 1 do begin
      if Units[I + 1].Priority < Units[I].Priority then begin
        tmpU := TProjUnit.Create(Self);
        tmpU.Assign(Units[I]);
        Units[I].Assign(Units[I + 1]);
        Units[I + 1].Assign(tmpU);
        tmpU.Free;
        Again := True;
      end;
      Inc(I);
    end;
  until not Again;
end;

procedure TProject.IncrementBuildNumber;
begin
  Inc(fOptions.VersionInfo.Build);
  fOptions.VersionInfo.FileVersion := Format('%d.%d.%d.%d', [fOptions.VersionInfo.Major, fOptions.VersionInfo.Minor,
    fOptions.VersionInfo.Release, fOptions.VersionInfo.Build]);
  if (fOptions.VersionInfo.SyncProduct) then
    fOptions.VersionInfo.ProductVersion := Format('%d.%d.%d.%d', [fOptions.VersionInfo.Major,
      fOptions.VersionInfo.Minor, fOptions.VersionInfo.Release, fOptions.VersionInfo.Build]);
  SetModified(True);
end;

{ TUnitList }

constructor TUnitList.Create;
begin
  inherited Create;
  fList := TList.Create;
end;

destructor TUnitList.Destroy;
var
  I: integer;
begin
  for I := 0 to fList.Count - 1 do
    TProjUnit(fList[I]).Free;
  fList.Free;
  inherited;
end;

function TUnitList.Add(aunit: TProjUnit): integer;
begin
  result := fList.Add(aunit);
end;

procedure TUnitList.Remove(index: integer);
begin
  fList.Delete(index);
  fList.Pack;
  fList.Capacity := fList.Count;
end;

function TUnitList.GetCount: integer;
begin
  result := fList.Count;
end;

function TUnitList.GetItem(index: integer): TProjUnit;
begin
  result := TProjUnit(fList[index]);
end;

function TUnitList.IndexOf(Editor: TEditor): integer;
begin
  result := IndexOf(editor.FileName);
end;

function TUnitList.IndexOf(const FileName: AnsiString): integer;
var
  s1, s2: AnsiString;
begin
  for result := 0 to fList.Count - 1 do begin
    s1 := GetRealPath(TProjUnit(fList[result]).FileName, TProjUnit(fList[result]).fParent.Directory);
    s2 := GetRealPath(FileName, TProjUnit(fList[result]).fParent.Directory);
    if SameText(s1, s2) then
      exit;
  end;
  result := -1;
end;

end.

