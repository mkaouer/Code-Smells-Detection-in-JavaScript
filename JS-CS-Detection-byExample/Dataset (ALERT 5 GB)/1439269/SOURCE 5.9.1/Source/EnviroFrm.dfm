object EnviroForm: TEnviroForm
  Left = 835
  Top = 465
  BorderStyle = bsDialog
  Caption = 'Environment Options'
  ClientHeight = 462
  ClientWidth = 484
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -12
  Font.Name = 'Segoe UI'
  Font.Style = []
  OldCreateOrder = False
  Position = poMainFormCenter
  OnClose = FormClose
  OnCreate = FormCreate
  DesignSize = (
    484
    462)
  PixelsPerInch = 96
  TextHeight = 15
  object btnOk: TBitBtn
    Left = 210
    Top = 430
    Width = 85
    Height = 25
    Anchors = [akRight, akBottom]
    Caption = 'OK'
    Default = True
    ModalResult = 1
    TabOrder = 1
    OnClick = btnOkClick
    Glyph.Data = {
      DE010000424DDE01000000000000760000002800000024000000120000000100
      0400000000006801000000000000000000001000000000000000000000000000
      80000080000000808000800000008000800080800000C0C0C000808080000000
      FF0000FF000000FFFF00FF000000FF00FF00FFFF0000FFFFFF00333333333333
      3333333333333333333333330000333333333333333333333333F33333333333
      00003333344333333333333333388F3333333333000033334224333333333333
      338338F3333333330000333422224333333333333833338F3333333300003342
      222224333333333383333338F3333333000034222A22224333333338F338F333
      8F33333300003222A3A2224333333338F3838F338F33333300003A2A333A2224
      33333338F83338F338F33333000033A33333A222433333338333338F338F3333
      0000333333333A222433333333333338F338F33300003333333333A222433333
      333333338F338F33000033333333333A222433333333333338F338F300003333
      33333333A222433333333333338F338F00003333333333333A22433333333333
      3338F38F000033333333333333A223333333333333338F830000333333333333
      333A333333333333333338330000333333333333333333333333333333333333
      0000}
    NumGlyphs = 2
  end
  object btnCancel: TBitBtn
    Left = 300
    Top = 430
    Width = 85
    Height = 25
    Anchors = [akRight, akBottom]
    TabOrder = 3
    Kind = bkCancel
  end
  object btnHelp: TBitBtn
    Left = 390
    Top = 430
    Width = 85
    Height = 25
    Anchors = [akRight, akBottom]
    Enabled = False
    TabOrder = 2
    OnClick = btnHelpClick
    Kind = bkHelp
  end
  object PagesMain: TPageControl
    Left = 0
    Top = 0
    Width = 484
    Height = 425
    ActivePage = tabGeneral
    HotTrack = True
    TabOrder = 0
    object tabGeneral: TTabSheet
      Caption = 'General'
      ParentShowHint = False
      ShowHint = False
      DesignSize = (
        476
        395)
      object lblMRU: TLabel
        Left = 312
        Top = 14
        Width = 126
        Height = 15
        AutoSize = False
        Caption = 'Max Files in History List:'
      end
      object lblMsgTabs: TLabel
        Left = 302
        Top = 62
        Width = 160
        Height = 15
        AutoSize = False
        Caption = 'Editor Tab Location:'
      end
      object lblLang: TLabel
        Left = 302
        Top = 110
        Width = 160
        Height = 15
        AutoSize = False
        Caption = 'Language'
      end
      object lblTheme: TLabel
        Left = 302
        Top = 158
        Width = 160
        Height = 13
        AutoSize = False
        Caption = 'Theme'
      end
      object UIfontlabel: TLabel
        Left = 248
        Top = 208
        Width = 39
        Height = 15
        Caption = 'UI font:'
      end
      object cbBackups: TCheckBox
        Left = 16
        Top = 37
        Width = 265
        Height = 17
        Anchors = [akLeft, akTop, akRight]
        Caption = 'Create File Backups'
        ParentShowHint = False
        ShowHint = True
        TabOrder = 1
      end
      object cbMinOnRun: TCheckBox
        Left = 16
        Top = 58
        Width = 265
        Height = 17
        Anchors = [akLeft, akTop, akRight]
        Caption = 'Minimize on Run'
        ParentShowHint = False
        ShowHint = True
        TabOrder = 2
      end
      object cbDefCpp: TCheckBox
        Left = 16
        Top = 16
        Width = 265
        Height = 17
        Anchors = [akLeft, akTop, akRight]
        Caption = 'Default to C++ on New Project'
        ParentShowHint = False
        ShowHint = True
        TabOrder = 0
      end
      object cbShowBars: TCheckBox
        Left = 16
        Top = 80
        Width = 265
        Height = 17
        Anchors = [akLeft, akTop, akRight]
        Caption = 'Show Toolbars in Full Screen'
        ParentShowHint = False
        ShowHint = True
        TabOrder = 3
      end
      object cbMultiLineTab: TCheckBox
        Left = 16
        Top = 101
        Width = 265
        Height = 17
        Anchors = [akLeft, akTop, akRight]
        Caption = 'Enable Editor Multiline Tabs'
        ParentShowHint = False
        ShowHint = True
        TabOrder = 4
      end
      object rgbAutoOpen: TRadioGroup
        Left = 246
        Top = 272
        Width = 215
        Height = 109
        Caption = 'Auto Open'
        Items.Strings = (
          'All project files'
          'Only first project file'
          'Opened files at previous closing'
          'None')
        TabOrder = 16
      end
      object gbDebugger: TGroupBox
        Left = 15
        Top = 240
        Width = 215
        Height = 53
        Caption = 'Debug Variable Browser'
        TabOrder = 8
        object cbWatchHint: TCheckBox
          Left = 14
          Top = 22
          Width = 195
          Height = 17
          Caption = 'Watch variable under mouse'
          TabOrder = 0
        end
      end
      object cbNoSplashScreen: TCheckBox
        Left = 16
        Top = 122
        Width = 265
        Height = 17
        Caption = 'No Splash Screen on startup'
        TabOrder = 5
      end
      object gbProgress: TGroupBox
        Left = 15
        Top = 311
        Width = 215
        Height = 70
        Caption = 'Compilation Progress Window '
        TabOrder = 9
        object cbShowProgress: TCheckBox
          Left = 14
          Top = 22
          Width = 195
          Height = 17
          Caption = '&Show during compilation'
          TabOrder = 0
        end
        object cbAutoCloseProgress: TCheckBox
          Left = 14
          Top = 43
          Width = 195
          Height = 17
          Caption = '&Auto close after compile'
          TabOrder = 1
        end
      end
      object seMRUMax: TSpinEdit
        Left = 408
        Top = 32
        Width = 51
        Height = 24
        MaxLength = 2
        MaxValue = 0
        MinValue = 0
        TabOrder = 10
        Value = 0
      end
      object cboTabsTop: TComboBox
        Left = 302
        Top = 80
        Width = 160
        Height = 23
        Style = csDropDownList
        ItemHeight = 15
        TabOrder = 11
        Items.Strings = (
          'Top'
          'Bottom'
          'Left'
          'Right')
      end
      object cboLang: TComboBox
        Left = 302
        Top = 128
        Width = 160
        Height = 23
        Style = csDropDownList
        ItemHeight = 15
        TabOrder = 12
      end
      object cboTheme: TComboBox
        Left = 302
        Top = 176
        Width = 160
        Height = 23
        Style = csDropDownList
        ItemHeight = 15
        TabOrder = 13
      end
      object cbUIfont: TComboBox
        Left = 248
        Top = 232
        Width = 153
        Height = 26
        AutoComplete = False
        Style = csOwnerDrawVariable
        DropDownCount = 10
        ItemHeight = 20
        Sorted = True
        TabOrder = 14
        OnDrawItem = cbUIfontDrawItem
      end
      object cbUIfontsize: TComboBox
        Left = 410
        Top = 232
        Width = 47
        Height = 26
        AutoComplete = False
        Style = csOwnerDrawVariable
        DropDownCount = 10
        ItemHeight = 20
        TabOrder = 15
        OnChange = cbUIfontsizeChange
        OnDrawItem = cbUIfontsizeDrawItem
        Items.Strings = (
          '7'
          '8'
          '9'
          '10'
          '11'
          '12'
          '13'
          '14'
          '15'
          '16')
      end
      object cbPauseConsole: TCheckBox
        Left = 16
        Top = 143
        Width = 265
        Height = 17
        Caption = 'Pause console programs after return'
        TabOrder = 6
      end
      object cbCheckAssocs: TCheckBox
        Left = 16
        Top = 164
        Width = 265
        Height = 17
        Caption = 'Check file associations on startup'
        TabOrder = 7
      end
    end
    object tabPaths: TTabSheet
      Caption = 'Directories'
      ParentShowHint = False
      ShowHint = False
      object lblUserDir: TLabel
        Left = 8
        Top = 80
        Width = 400
        Height = 15
        AutoSize = False
        Caption = 'User'#39's Default Directory'
      end
      object lblTemplatesDir: TLabel
        Left = 8
        Top = 144
        Width = 400
        Height = 15
        AutoSize = False
        Caption = 'Templates Directory'
      end
      object lblSplash: TLabel
        Left = 8
        Top = 337
        Width = 400
        Height = 15
        AutoSize = False
        Caption = 'Splash Screen Image'
      end
      object lblIcoLib: TLabel
        Left = 8
        Top = 208
        Width = 400
        Height = 15
        AutoSize = False
        Caption = 'Icon Library Path'
      end
      object lblLangPath: TLabel
        Left = 8
        Top = 273
        Width = 400
        Height = 15
        AutoSize = False
        Caption = 'Language Path'
      end
      object btnDefBrws: TSpeedButton
        Tag = 1
        Left = 438
        Top = 100
        Width = 23
        Height = 22
        Glyph.Data = {
          36030000424D3603000000000000360000002800000010000000100000000100
          18000000000000030000120B0000120B00000000000000000000BFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000000000
          000000000000000000000000000000000000000000000000000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF00000000AEFF0096DB0096DB0096DB0096DB0096DB00
          96DB0096DB0082BE000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF00AEFF00AEFF00AEFF00
          AEFF00AEFF0096DB000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF5DCCFF5DCCFF5DCCFF5D
          CCFF5DCCFF00AEFF000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF686868BDEBFF
          5DCCFF5DCCFF000000000000000000000000000000000000BFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBF000000000000000000BFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF}
        OnClick = BrowseClick
      end
      object btnOutputbrws: TSpeedButton
        Tag = 2
        Left = 437
        Top = 164
        Width = 23
        Height = 22
        Glyph.Data = {
          36030000424D3603000000000000360000002800000010000000100000000100
          18000000000000030000120B0000120B00000000000000000000BFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000000000
          000000000000000000000000000000000000000000000000000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF00000000AEFF0096DB0096DB0096DB0096DB0096DB00
          96DB0096DB0082BE000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF00AEFF00AEFF00AEFF00
          AEFF00AEFF0096DB000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF5DCCFF5DCCFF5DCCFF5D
          CCFF5DCCFF00AEFF000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF686868BDEBFF
          5DCCFF5DCCFF000000000000000000000000000000000000BFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBF000000000000000000BFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF}
        OnClick = BrowseClick
      end
      object btnBrwIcon: TSpeedButton
        Tag = 3
        Left = 437
        Top = 228
        Width = 23
        Height = 22
        Glyph.Data = {
          36030000424D3603000000000000360000002800000010000000100000000100
          18000000000000030000120B0000120B00000000000000000000BFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000000000
          000000000000000000000000000000000000000000000000000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF00000000AEFF0096DB0096DB0096DB0096DB0096DB00
          96DB0096DB0082BE000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF00AEFF00AEFF00AEFF00
          AEFF00AEFF0096DB000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF5DCCFF5DCCFF5DCCFF5D
          CCFF5DCCFF00AEFF000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF686868BDEBFF
          5DCCFF5DCCFF000000000000000000000000000000000000BFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBF000000000000000000BFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF}
        OnClick = BrowseClick
      end
      object btnBrwLang: TSpeedButton
        Tag = 5
        Left = 437
        Top = 293
        Width = 23
        Height = 22
        Glyph.Data = {
          36030000424D3603000000000000360000002800000010000000100000000100
          18000000000000030000120B0000120B00000000000000000000BFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000000000
          000000000000000000000000000000000000000000000000000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF00000000AEFF0096DB0096DB0096DB0096DB0096DB00
          96DB0096DB0082BE000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF00AEFF00AEFF00AEFF00
          AEFF00AEFF0096DB000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF5DCCFF5DCCFF5DCCFF5D
          CCFF5DCCFF00AEFF000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF686868BDEBFF
          5DCCFF5DCCFF000000000000000000000000000000000000BFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBF000000000000000000BFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF}
        OnClick = BrowseClick
      end
      object btnBrwSplash: TSpeedButton
        Tag = 4
        Left = 437
        Top = 357
        Width = 23
        Height = 22
        Glyph.Data = {
          36030000424D3603000000000000360000002800000010000000100000000100
          18000000000000030000120B0000120B00000000000000000000BFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF0000000000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000BF
          BFBF000000BFBFBF0000005DCCFF5DCCFF5DCCFF000000BFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBF000000BFBFBFBFBFBFBFBFBFBFBFBF6868680000000000
          00000000000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF000000000000
          000000000000000000000000000000000000000000000000000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF00000000AEFF0096DB0096DB0096DB0096DB0096DB00
          96DB0096DB0082BE000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF00AEFF00AEFF00AEFF00
          AEFF00AEFF0096DB000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF0000005DCCFF
          00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF00AEFF0096DB000000BFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBF0000005DCCFF00AEFF00AEFF5DCCFF5DCCFF5DCCFF5D
          CCFF5DCCFF00AEFF000000BFBFBFBFBFBFBFBFBFBFBFBFBFBFBF686868BDEBFF
          5DCCFF5DCCFF000000000000000000000000000000000000BFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBF000000000000000000BFBFBFBFBFBFBFBFBFBF
          BFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBFBF}
        OnClick = BrowseClick
      end
      object lblOptionsDir: TLabel
        Left = 8
        Top = 10
        Width = 457
        Height = 31
        AutoSize = False
        Caption = 'Current Options directory. Click the button to reset Dev-C++.'
      end
      object edUserDir: TEdit
        Left = 16
        Top = 100
        Width = 409
        Height = 23
        ReadOnly = True
        TabOrder = 0
        Text = 'edUserDir'
      end
      object edTemplatesDir: TEdit
        Left = 16
        Top = 164
        Width = 409
        Height = 23
        ReadOnly = True
        TabOrder = 1
        Text = 'edTemplatesDir'
      end
      object edSplash: TEdit
        Left = 16
        Top = 357
        Width = 409
        Height = 23
        ReadOnly = True
        TabOrder = 2
        Text = 'edSplash'
      end
      object edIcoLib: TEdit
        Left = 16
        Top = 228
        Width = 409
        Height = 23
        ReadOnly = True
        TabOrder = 3
        Text = 'edIcoLib'
      end
      object edLang: TEdit
        Left = 16
        Top = 293
        Width = 409
        Height = 23
        ReadOnly = True
        TabOrder = 4
        Text = 'edLang'
      end
      object edOptionsDir: TEdit
        Left = 16
        Top = 34
        Width = 281
        Height = 23
        ReadOnly = True
        TabOrder = 5
        Text = 'edOptionsDir'
      end
      object btnResetDev: TButton
        Left = 304
        Top = 32
        Width = 155
        Height = 25
        Caption = 'Remove settings and exit'
        TabOrder = 6
        OnClick = btnResetDevClick
      end
    end
    object tabExternal: TTabSheet
      Caption = 'External Programs'
      DesignSize = (
        476
        395)
      object lblExternal: TLabel
        Left = 8
        Top = 8
        Width = 165
        Height = 15
        Caption = 'External programs associations:'
      end
      object btnExtAdd: TSpeedButton
        Left = 128
        Top = 364
        Width = 99
        Height = 25
        Anchors = [akBottom]
        Caption = 'Add'
        OnClick = btnExtAddClick
      end
      object btnExtDel: TSpeedButton
        Left = 261
        Top = 364
        Width = 99
        Height = 25
        Anchors = [akBottom]
        Caption = 'Delete'
        OnClick = btnExtDelClick
      end
      object vleExternal: TValueListEditor
        Left = 28
        Top = 30
        Width = 417
        Height = 330
        Anchors = [akLeft, akTop, akRight, akBottom]
        KeyOptions = [keyEdit, keyAdd, keyDelete]
        Options = [goVertLine, goHorzLine, goEditing, goAlwaysShowEditor, goThumbTracking]
        TabOrder = 0
        TitleCaptions.Strings = (
          'Extension'
          'External program')
        OnEditButtonClick = vleExternalEditButtonClick
        OnValidate = vleExternalValidate
        ColWidths = (
          84
          327)
      end
    end
    object tabAssocs: TTabSheet
      Caption = 'File Associations'
      ParentShowHint = False
      ShowHint = False
      DesignSize = (
        476
        395)
      object lblAssocFileTypes: TLabel
        Left = 8
        Top = 8
        Width = 55
        Height = 15
        Caption = 'File Types:'
      end
      object lblAssocDesc: TLabel
        Left = 0
        Top = 352
        Width = 476
        Height = 43
        Alignment = taCenter
        AutoSize = False
        Caption = 
          'Just check or un-check for which file types Dev-C++ will be regi' +
          'stered as the default application to open them...'
        WordWrap = True
      end
      object lstAssocFileTypes: TCheckListBox
        Left = 28
        Top = 30
        Width = 417
        Height = 320
        Anchors = [akLeft, akTop, akRight, akBottom]
        ItemHeight = 15
        TabOrder = 0
      end
    end
  end
  object dlgPic: TOpenPictureDialog
    Left = 14
    Top = 426
  end
end
