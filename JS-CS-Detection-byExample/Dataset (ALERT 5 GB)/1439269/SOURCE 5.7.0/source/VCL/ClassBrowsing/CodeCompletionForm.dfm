object CodeComplForm: TCodeComplForm
  Left = 332
  Top = 305
  BorderIcons = []
  BorderStyle = bsNone
  ClientHeight = 286
  ClientWidth = 472
  Color = clBtnFace
  Constraints.MinHeight = 128
  Constraints.MinWidth = 256
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -13
  Font.Name = 'Courier New'
  Font.Style = []
  FormStyle = fsStayOnTop
  KeyPreview = True
  OldCreateOrder = False
  OnDeactivate = FormDeactivate
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 16
  object lbCompletion: TListBox
    Left = 0
    Top = 0
    Width = 472
    Height = 286
    Style = lbOwnerDrawFixed
    AutoComplete = False
    Align = alClient
    BevelInner = bvNone
    BevelOuter = bvNone
    BorderStyle = bsNone
    ExtendedSelect = False
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -13
    Font.Name = 'Courier New'
    Font.Style = []
    ItemHeight = 16
    ParentFont = False
    TabOrder = 0
    OnDblClick = lbCompletionDblClick
    OnDrawItem = lbCompletionDrawItem
    OnKeyPress = lbCompletionKeyPress
  end
end
