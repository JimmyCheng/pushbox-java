unit about;

interface

uses
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
  ExtCtrls, StdCtrls, MPlayer;

type
  TfrmAbout = class(TForm)
    Image1: TImage;
    Button1: TButton;
    Panel1: TPanel;
    Timer1: TTimer;
    Label2: TLabel;
    Mp1: TMediaPlayer;
    OpenD: TOpenDialog;
    procedure Button1Click(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure drawgif;
    procedure Timer1Timer(Sender: TObject);
    procedure Label1DblClick(Sender: TObject);
    procedure FormShow(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure Mp1Notify(Sender: TObject);
    procedure Label1Click(Sender: TObject);
  private
    { Private declarations }
  public
    box:array[1..7,1..7] of integer;
    exitflag:boolean;
    path:string;
    { Public declarations }
  end;

var
  frmAbout: TfrmAbout;

implementation

uses box;

{$R *.DFM}

procedure TfrmAbout.Button1Click(Sender: TObject);
begin
  close;
end;

procedure TfrmAbout.FormCreate(Sender: TObject);
begin
  exitflag := true;
  timer1.enabled := false;
  timer1.tag := 0;
  path := frmMain.comdir+'\sound\backsound.wav';
  box[1][1] := 0;box[1][2] := 1;box[1][3] := 1;box[1][4] := 1;
  box[1][5] := 1;box[1][6] := 0;box[1][7] := 0;
  box[2][1] := 0;box[2][2] := 1;box[2][3] := 2;box[2][4] := 2;
  box[2][5] := 1;box[2][6] := 1;box[2][7] := 1;
  box[3][1] := 1;box[3][2] := 1;box[3][3] := 2;box[3][4] := 4;
  box[3][5] := 3;box[3][6] := 57;box[3][7] := 1;
  box[4][1] := 1;box[4][2] := 2;box[4][3] := 4;box[4][4] := 1;
  box[4][5] := 2;box[4][6] := 2;box[4][7] := 1;
  box[5][1] := 1;box[5][2] := 2;box[5][3] := 2;box[5][4] := 2;
  box[5][5] := 2;box[5][6] := 1;box[5][7] := 1;
  box[6][1] := 1;box[6][2] := 1;box[6][3] := 1;box[6][4] := 2;
  box[6][5] := 2;box[6][6] := 1;box[6][7] := 0;
  box[7][1] := 0;box[7][2] := 0;box[7][3] := 1;box[7][4] := 1;
  box[7][5] := 1;box[7][6] := 1;box[7][7] := 0;
  drawgif;
  timer1.enabled := true;
end;

procedure tfrmAbout.drawgif;
var
   i,j:integer;
   comimage:timage;
begin
  for i := 1 to 7 do
    for j := 1 to 7 do
    begin
      case box[i][j] of
        0: comimage := frmMain.imgBlack;
        1: comimage := frmMain.imgWall;
        2: comimage := frmMain.imgFloor;
        3: comimage := frmMain.imgBox;
        4: comimage := frmMain.imgBall;
        7: comimage := frmMain.imgBoxFull;
        51, 61: comimage := frmMain.imgPushR1;
        52, 62: comimage := frmMain.imgPushR2;
        53, 63: comimage := frmMain.imgPushL1;
        54, 64: comimage := frmMain.imgPushL2;
        55, 65:comimage := frmMain.imgPushU1;
        56, 66:comimage := frmMain.imgPushU2;
        57, 67:comimage := frmMain.imgPushD1;
        58, 68:comimage := frmMain.imgPushD2;
      else
        exit;
      end;
      image1.Canvas.CopyRect(rect((j-1)*30,(i-1)*30,j*30,i*30),
                                comimage.canvas,
                                rect(0,0,30,30))
    end;
end;

procedure TfrmAbout.Timer1Timer(Sender: TObject);
begin
  timer1.enabled := false;
  timer1.tag := timer1.tag+1;
  case timer1.tag of
    1:begin box[3][4] := 7;box[3][5] := 53;box[3][6] := 2;end;
    2:begin box[3][3] := 3;box[3][4] := 53;box[3][5] := 2;end;
    3:begin box[3][4] := 4;box[2][4] := 55;end;
    4:begin box[2][3] := 53;box[2][4] := 2;end;
    5:begin box[2][3] := 2;box[3][3] := 57;box[4][3] := 7;end;
    6:begin box[3][3] := 2;box[4][3] := 57;box[5][3] := 3;end;
    7:begin box[4][3] := 4;box[4][2] := 53;end;
    8:begin box[4][2] := 2;box[5][2] := 57;end;
    9:begin box[5][2] := 2;box[5][3] := 51;box[5][4] := 7;end;
    10:begin box[5][3] := 2;box[5][4] := 51;box[5][5] := 3;end;
    11:begin box[5][4] := 4;box[6][4] := 57;end;
    12:begin box[6][4] := 2;box[6][5] := 51;end;
    13:begin box[6][5] := 2;box[5][5] := 55;box[4][5] := 7;end;
    14:begin box[5][5] := 2;box[4][5] := 55;box[3][5] := 3;end;
    15:begin box[4][5] := 4;box[4][6] := 51;end;
    16:begin box[4][6] := 2;box[3][6] := 55;timer1.tag := 0;end;
  end;
  drawgif;
  timer1.enabled := true;
end;

procedure TfrmAbout.Label1DblClick(Sender: TObject);
var tempstring:string;
    value:integer;
begin
timer1.enabled := false;
 if inputquery('Input','please set the animationn inteval.',tempstring) then
 begin
   value := strtoint(tempstring);
   if (0<value) and (value<1000) then
      timer1.interval := value;
 end;
timer1.enabled := true;
end;

procedure TfrmAbout.FormShow(Sender: TObject);
begin
exitflag := true;
mp1.FileName  := path;
mp1.Open;
mp1.Play;
end;

procedure TfrmAbout.FormClose(Sender: TObject; var Action: TCloseAction);
begin
exitflag := false;
mp1.Stop;
end;

procedure TfrmAbout.Mp1Notify(Sender: TObject);
begin
if exitflag then
begin
  mp1.filename := path;
  mp1.Open;
  mp1.Play;
end;
exitflag := true;
end;

procedure TfrmAbout.Label1Click(Sender: TObject);
begin
opend.InitialDir := frmMain.comdir + '\sound';
if opend.Execute then
   begin
     exitflag := false;
     mp1.stop;
     path := opend.FileName;
     mp1.FileName := path;
     mp1.Open;
     mp1.Play;
   end;
end;

end.
