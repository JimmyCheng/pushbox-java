program pushbox;

uses
  Forms,
  box in 'box.pas' {frmMain},
  design in 'design.pas' {frmDesign},
  about in 'about.pas' {frmAbout};

{$R *.RES}

begin
  Application.Initialize;
  Application.CreateForm(TfrmMain, frmMain);
  Application.CreateForm(TfrmDesign, frmDesign);
  Application.CreateForm(TfrmAbout, frmAbout);
  Application.Run;
end.
