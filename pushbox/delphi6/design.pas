unit design;

interface

uses
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
  ExtCtrls, StdCtrls;

type
  TfrmDesign = class(TForm)
    bkground: TImage;
    Panel1: TPanel;
    black: TImage;
    boxok: TImage;
    diban: TImage;
    pushd1: TImage;
    qiang: TImage;
    ybox: TImage;
    rbox: TImage;
    Shape1: TShape;
    Shape2: TShape;
    Shape4: TShape;
    Shape5: TShape;
    Shape6: TShape;
    Shape7: TShape;
    Shape8: TShape;
    Shape9: TShape;
    Panel2: TPanel;
    Image1: TImage;
    Shape16: TShape;
    Shape17: TShape;
    btnSave: TButton;
    btnReset: TButton;
    btnExit: TButton;
    Label1: TLabel;
    Label2: TLabel;
    procedure FormShow(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure blackClick(Sender: TObject);
    procedure boxokClick(Sender: TObject);
    procedure dibanClick(Sender: TObject);
    procedure yboxClick(Sender: TObject);
    procedure pushd1Click(Sender: TObject);
    procedure qiangClick(Sender: TObject);
    procedure rboxClick(Sender: TObject);
    procedure bkgroundMouseDown(Sender: TObject; Button: TMouseButton;
      Shift: TShiftState; X, Y: Integer);
    procedure btnSaveClick(Sender: TObject);
    procedure btnResetClick(Sender: TObject);
    procedure btnExitClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
  private
    { Private declarations }
  public
  designbox:array [1..14,1..16] of integer;
  selimage:integer;
  boxnum,ballnum: integer;
  manx, many: integer;
  saveflag:integer;
    { Public declarations }
  end;

var
  frmDesign: TfrmDesign;

implementation
uses box;
{$R *.DFM}

procedure TfrmDesign.FormShow(Sender: TObject);
var
  i,j:integer;
begin
  for i := 1 to 16 do
    for j := 1 to 14 do
    begin
        bkground.canvas.copyrect(rect((i-1)*30, (j-1)*30, i*30, j*30),
                                 black.canvas,
                                 rect(0,0,30,30));
        designbox[i][j] := 0;
    end;

  image1.canvas.CopyRect(rect(0,0,30,30),
                        black.canvas,
                        rect(0,0,30,30));
   selimage := 0;
   boxnum := 0;
   ballnum := 0;
   saveflag := 1;
   manx := 0;
   many := 0;

   bkground.Canvas.Pen.color := clyellow;

   for i := 1 to 17 do
   begin
     bkground.canvas.MoveTo((i-1)*30,0);
     bkground.canvas.lineto((i-1)*30,420);
   end;

  for i := 1 to 15 do
  begin
     bkground.canvas.MoveTo(0, (i-1)*30);
     bkground.canvas.lineTo(480, (i-1)*30);
  end;
end;

procedure TfrmDesign.FormClose(Sender: TObject; var Action: TCloseAction);
begin
   modalresult := saveflag;
end;

procedure TfrmDesign.blackClick(Sender: TObject);
begin
 image1.canvas.CopyRect(rect(0,0,30,30),
                        black.canvas,
                        rect(0,0,30,30));
 selimage := 0;
end;

procedure TfrmDesign.boxokClick(Sender: TObject);
begin
  image1.canvas.CopyRect(rect(0,0,30,30),
                        boxok.canvas,
                        rect(0,0,30,30));
  selimage := 4;
end;

procedure TfrmDesign.dibanClick(Sender: TObject);
begin
  image1.canvas.CopyRect(rect(0,0,30,30),
                        diban.canvas,
                        rect(0,0,30,30));
  selimage := 2;
end;

procedure TfrmDesign.yboxClick(Sender: TObject);
begin
  image1.canvas.CopyRect(rect(0, 0, 30, 30),
                        ybox.canvas,
                        rect(0, 0, 30, 30));
  selimage := 3;
end;

procedure TfrmDesign.pushd1Click(Sender: TObject);
begin
  image1.canvas.CopyRect(rect(0, 0, 30, 30),
                        pushd1.canvas,
                        rect(0, 0, 30, 30));
  selimage := 57;
end;

procedure TfrmDesign.qiangClick(Sender: TObject);
begin
  image1.canvas.CopyRect(rect(0, 0, 30, 30),
                        qiang.canvas,
                        rect(0, 0, 30, 30));
  selimage := 1;
end;

procedure TfrmDesign.rboxClick(Sender: TObject);
begin
  image1.canvas.CopyRect(rect(0, 0, 30, 30),
                        rbox.canvas,
                        rect(0, 0, 30, 30));
  selimage := 7;
end;

procedure TfrmDesign.bkgroundMouseDown(Sender: TObject; Button: TMouseButton;
  Shift: TShiftState; X, Y: Integer);
var
  lx, ly:integer;
begin

  lx := x div 30;
  ly := y div 30;
  bkground.canvas.copyrect(rect(lx*30, ly*30, lx*30 + 30, ly*30 + 30),
                           image1.canvas,
                           rect(0,0,30,30));

  if designbox[lx + 1][ly + 1] = 57 then
  begin
    manx := 0;
    many := 0;
  end;

  designbox[lx + 1][ly + 1] := selimage;

  if selimage = 57 then
  begin
       if manx<>0 then
       begin
          bkground.canvas.copyrect(rect(manx*30-30,many*30-30,manx*30,many*30),
                                diban.canvas,
                                 rect(0,0,30,30));
          designbox[many][manx] := 2;
       end;
       manx := lx + 1;
       many := ly + 1;
  end;
end;

procedure TfrmDesign.btnSaveClick(Sender: TObject);
var
  wf: file of integer;
  i, j, num: integer;
  filename: string;
begin
   if manx = 0 then
   begin
     showmessage('Please select a spirit£¡');
     exit;
   end;

   ballnum := 0;
   boxnum := 0;
   for i := 1 to 16 do
     for j := 1 to 14 do
     begin
       if designbox[i][j]=4 then
         inc(ballnum);

       if designbox[i][j]=3 then
         inc(boxnum);
     end;

   if ballnum <> boxnum then
   begin
    showmessage('The number of boxes don''t match the number of balls£¡');
    exit;
   end;

   num := frmMain.taskID + 1;
   filename := frmMain.comdir + '\task\task' + inttostr(num) + '.tsk';
   //find the last file
   while fileexists(filename) do
   begin
    num := num + 1;
    filename := frmMain.comdir + '\task\task' + inttostr(num) + '.tsk';
   end;

   assignfile(wf,filename);
   rewrite(wf);
   for i := 1 to 16 do
     for j := 1 to 14 do
       write(wf,designbox[i][j]);
   closefile(wf);
   
   showmessage('The design has been saved to ' + filename);
   btnResetClick(nil);
   saveflag := 2;
end;

procedure TfrmDesign.btnResetClick(Sender: TObject);
begin
   formshow(nil);
end;

procedure TfrmDesign.btnExitClick(Sender: TObject);
begin
 frmDesign.close;
end;

procedure TfrmDesign.FormCreate(Sender: TObject);
begin
saveflag := 1;
end;

end.
