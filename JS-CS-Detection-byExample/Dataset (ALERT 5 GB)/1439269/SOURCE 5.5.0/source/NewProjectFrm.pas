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

{$WARN UNIT_PLATFORM OFF}
unit NewProjectFrm;

interface

uses
{$IFDEF WIN32}
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
  StdCtrls, ExtCtrls, ImgList, Buttons, ComCtrls, Templates, Inifiles;
{$ENDIF}
{$IFDEF LINUX}
  SysUtils, Classes, QGraphics, QControls, QForms, QDialogs,
  QStdCtrls, QExtCtrls, QImgList, QButtons, QComCtrls, Templates, Inifiles;
{$ENDIF}

type
  TNewProjectForm = class(TForm)
    btnOk: TBitBtn;
    btnCancel: TBitBtn;
    rbC: TRadioButton;
    rbCpp: TRadioButton;
    cbDefault: TCheckBox;
    lblPrjName: TLabel;
    edProjectName: TEdit;
    TabsMain: TTabControl;
    ProjView: TListView;
    TemplateLabel: TLabel;
    btnHelp: TBitBtn;
    ImageList: TImageList;
    procedure ProjViewChange(Sender: TObject; Item: TListItem;Change: TItemChange);
    procedure FormCreate(Sender: TObject);
    procedure LoadText;
    procedure FormDestroy(Sender: TObject);
    procedure TabsMainChange(Sender: TObject);
    procedure ProjViewDblClick(Sender: TObject);
    procedure btnHelpClick(Sender: TObject);
    procedure edProjectNameChange(Sender: TObject);
   private
    procedure AddTemplate(FileName: AnsiString);
    procedure ReadTemplateIndex;
   private
    fTemplates: TList;
    procedure UpdateView;
   public
    function GetTemplate: TTemplate;
  end;

implementation

uses
{$IFDEF WIN32}
  MultiLangSupport, utils, datamod, FileCtrl, devcfg, version,
  project, prjtypes;
{$ENDIF}
{$IFDEF LINUX}
  MultiLangSupport, utils, datamod, devcfg, version,
  project, prjtypes;
{$ENDIF}

{$R *.dfm}

procedure TNewProjectForm.FormCreate(Sender: TObject);
begin
	fTemplates:= TList.Create;
	LoadText;
	ReadTemplateIndex;
	edProjectName.Text:= format(Lang[ID_NEWPROJECT], [dmMain.GetNewFileNumber]);
end;

procedure TNewProjectForm.FormDestroy(Sender: TObject);
var
	I : integer;
begin
	for I := 0 to fTemplates.Count - 1 do
		TTemplate(fTemplates[i]).Free;
	fTemplates.Free;
end;

procedure TNewProjectForm.AddTemplate(FileName: AnsiString);
var
	Template: TTemplate;
begin
	if not FileExists(FileName) then exit;
	Template:= TTemplate.Create;
	Template.ReadTemplateFile(FileName);
	fTemplates.Add(Template);
end;

procedure TNewProjectForm.ReadTemplateIndex;
var
  i: Integer;
  LTemplates: TStringList;
  sDir: AnsiString;
begin
  sDir:=devDirs.Templates;
  if not CheckChangeDir(sDir) then begin
    MessageDlg('Could not change to the Templates directory ('+devDirs.Templates+')...', mtError, [mbOk], 0);
    Exit;
  end;
  LTemplates:= TStringList.Create;
  try
   FilesFromWildCard(devDirs.Templates,'*'+TEMPLATE_EXT,LTemplates,FALSE,FALSE,TRUE);
   if LTemplates.Count> 0 then begin
      for i:= 0 to pred(LTemplates.Count) do
       AddTemplate(LTemplates[i]);
      UpdateView;
    end;
  finally
   LTemplates.Free;
  end;
end;

function TNewProjectForm.GetTemplate: TTemplate;
begin
	if assigned(ProjView.Selected) then begin
		result := TTemplate(fTemplates[integer(ProjView.Selected.Data)]);
		result.Options.useGPP := rbCpp.Checked;
		result.Name := edProjectName.Text;
	end else
		result := nil;
end;

procedure TNewProjectForm.ProjViewChange(Sender: TObject; Item: TListItem;Change: TItemChange);
var
	LTemplate: TTemplate;
begin
	if Assigned(ProjView.Selected) then begin
		LTemplate:= TTemplate(fTemplates[integer(ProjView.Selected.Data)]);
		if not assigned(LTemplate) then
			exit;
		TemplateLabel.Caption:= LTemplate.Description;

		//if edProjectName.Text = '' then // suggest a name?
		//	edProjectName.Text := LTemplate.Name;

		if LTemplate.Options.useGPP then begin
			rbC.Enabled := False;
			rbCpp.Checked := True;
		end else
			rbC.Enabled := True;
	end else
		TemplateLabel.Caption:= '';

	btnOk.Enabled := Assigned(ProjView.Selected) and (edProjectName.Text <> '');
end;

procedure TNewProjectForm.LoadText;
begin
	// Set interface font
	Font.Name := devData.InterfaceFont;
	Font.Size := devData.InterfaceFontSize;

	TemplateLabel.Font.Name := devData.InterfaceFont;
	TemplateLabel.Font.Size := devData.InterfaceFontSize;

	Caption :=              Lang[ID_NP];
	lblPrjName.Caption:=    Lang[ID_NP_PRJNAME];
	rbC.Caption :=          Lang[ID_NP_DEFAULTC];
	rbCpp.Caption :=        Lang[ID_NP_DEFAULTCPP];
	cbDefault.Caption :=    Lang[ID_NP_MAKEDEFAULT];

	btnOk.Caption :=        Lang[ID_BTN_OK];
	btnCancel.Caption :=    Lang[ID_BTN_CANCEL];
	btnHelp.Caption:=       Lang[ID_BTN_HELP];
end;

procedure TNewProjectForm.UpdateView;
 function HasPage(const value: AnsiString): boolean;
  var
   idx: integer;
  begin
    result:= TRUE;
    for idx:= 0 to pred(TabsMain.Tabs.Count) do
     if CompareText(TabsMain.Tabs[idx], Value) = 0 then exit;
    result:= FALSE;
  end;
var
 idx: integer;
 LTemplate: TTemplate;
 Item: TListItem;
 LIcon: TIcon;
 fName: AnsiString;
begin
	for idx:= 0 to pred(fTemplates.Count) do begin
		LTemplate:= TTemplate(fTemplates[idx]);
		if not HasPage(LTemplate.Category) then
			TabsMain.Tabs.Append(LTemplate.Category);
	end;

	ImageList.Clear;
	ProjView.Items.Clear;

	for idx := 0 to pred(fTemplates.Count) do begin
		LTemplate:= TTemplate(fTemplates[idx]);
		if LTemplate.Category = '' then
			LTemplate.Category:= Lang[ID_NP_PRJSHEET];
		if SameText(LTemplate.Category, TabsMain.Tabs[TabsMain.TabIndex]) then begin
			Item:= ProjView.Items.Add;
			Item.Caption:= LTemplate.Name;
			Item.Data:= pointer(idx);
			fName:= ValidateFile(LTemplate.Icon, '', true);
			if fName <> '' then begin
				LIcon:= TIcon.Create;
				try
					LIcon.LoadFromFile(fName); // ValidateFile prepends path
					Item.ImageIndex:= ImageList.AddIcon(LIcon);
					if Item.ImageIndex = -1 then
						Item.ImageIndex:= 0;
				finally
					LIcon.Free;
				end;
			end else
				Item.ImageIndex := 0;
		end;
	end;
end;

procedure TNewProjectForm.TabsMainChange(Sender: TObject);
begin
  UpdateView;
end;

procedure TNewProjectForm.ProjViewDblClick(Sender: TObject);
begin
  ModalResult:= mrOk;
end;

procedure TNewProjectForm.btnHelpClick(Sender: TObject);
begin
	OpenHelpFile;
end;

procedure TNewProjectForm.edProjectNameChange(Sender: TObject);
begin
	btnOk.Enabled := Assigned(ProjView.Selected) and (edProjectName.Text <> '');
end;

end.
