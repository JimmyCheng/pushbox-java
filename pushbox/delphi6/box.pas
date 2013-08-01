unit box;

interface

uses
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
  ComCtrls, ExtCtrls,Menus, StdCtrls,inifiles,shellapi,mmsystem, Buttons;
type
  boxbase = array [1..14, 1..16] of Byte;

  TfrmMain = class(TForm)
    StatusBar1: TStatusBar;
    mnuMain: TMainMenu;
    imgBlack: TImage;
    imgBall: TImage;
    imgFloor: TImage;
    imgPushD1: TImage;
    imgPushD2: TImage;
    imgPushL1: TImage;
    imgPushL2: TImage;
    imgPushR1: TImage;
    imgPushR2: TImage;
    imgPushU1: TImage;
    imgPushU2: TImage;
    imgWall: TImage;
    imgBox: TImage;
    imgBoxFull: TImage;
    mnuFunction: TMenuItem;
    mnuitDesign: TMenuItem;
    N4: TMenuItem;
    mnuUndo: TMenuItem;
    N6: TMenuItem;
    mnuRestart: TMenuItem;
    N8: TMenuItem;
    mnuExit: TMenuItem;
    mnuHelp: TMenuItem;
    N15: TMenuItem;
    mnuAbout: TMenuItem;
    bkground: TImage;
    tmrAnimate: TTimer;
    mnuDesign: TMenuItem;
    mnuSound: TMenuItem;
    N22: TMenuItem;
    procedure mnuExitClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure formKeyDown(Sender: TObject; var Key: Word;
      Shift: TShiftState);
    procedure mnuRestartClick(Sender: TObject);
    procedure mnuDesignClick(Sender: TObject);
    procedure mnuFunctionClick(Sender: TObject);
    procedure mnuitDesignClick(Sender: TObject);
    procedure mnuUndoClick(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure tmrAnimateTimer(Sender: TObject);
    procedure mnuHelpClick(Sender: TObject);
    procedure mnuAboutClick(Sender: TObject);
    procedure mnuSoundClick(Sender: TObject);
  private
    { Private declarations }
     procedure drawimage(argx, argy, argimage:Byte);
     procedure playSound(SoundType: integer);
     function readTask:integer;
     procedure drawbkground;
  public
     { Public declarations }
     lastX, lastY: integer;   //Previous Position
     currX, currY: integer;   //Current Postion
     boxmoved: boolean;         //whether the box is moved.e.g. spirit pushes the box to next position.
     comdir: string;           //cur directory
     taskID: integer;         //stage
     box: boxbase;             //the background tile.
     boxnum: integer;          //the number of un-located boxes.
     boxini: tinifile;         //ini file handle
     steps: integer;
     soundflag: boolean;
  end;
  CONST
     BG_BLACK = 0;
     BG_WALL  = 1;
     BG_FLOOR = 2;
     BOX_EMPTY= 3;
     BALL     = 4;
     BOX_FULL = 7;
     
     SOUND_BACKBOX   = 1;
     SOUND_BACKSOUND = 2;
     SOUND_CLICK     = 3;     
     SOUND_MOVE      = 4;
     SOUND_NOMOVE    = 5;
     SOUND_OVER      = 6;    
     SOUND_PUSHBOX   = 7;         
     SOUND_RETRY     = 8;     
     
     
var
  frmMain: TfrmMain;

implementation

uses design,about;

{$R *.DFM}
procedure tfrmMain.drawimage(argx, argy, argimage: Byte);
var
  comimage: TImage;
begin
  case argimage of
    BG_BLACK:  comimage := frmMain.imgBlack;
    BG_WALL:   comimage := frmMain.imgWall;
    BG_FLOOR:  comimage := frmMain.imgFloor;
    BOX_EMPTY: comimage := frmMain.imgBox;
    BALL:      comimage := frmMain.imgBall;
    BOX_FULL:  comimage := frmMain.imgBoxFull;

    //big number indicates there is ball in the the position
    51, 61: comimage := frmMain.imgPushU1;
    52, 62: comimage := frmMain.imgPushU2;
    53, 63: comimage := frmMain.imgPushL1;
    54, 64: comimage := frmMain.imgPushL2;
    55, 65: comimage := frmMain.imgPushR1;
    56, 66: comimage := frmMain.imgPushR2;
    57, 67: comimage := frmMain.imgPushD1;
    58, 68: comimage := frmMain.imgPushD2;
    else
      exit;
    end;
    frmMain.bkground.Canvas.CopyRect(rect((argy-1)*30, (argx-1)*30, argy*30, argx*30),
                                comimage.canvas,
                                rect(0, 0, 30, 30))
end;

function tfrmMain.readTask: integer;
var
  filehandle: file of Byte;
  i, j, fsize, funre: Byte;
  filename: string;
begin
  funre := 1;
  steps := 0;
  filename := comdir + '\task\task' + inttostr(taskID) + '.tsk';

  if (not FileExists(FileName)) then
  begin
     if taskID = 1 then
        funre := 0
     else
       begin
         taskID := 1;
         filename := comdir + '\task\task1.tsk';
         if not fileexists(filename) then
            funre := 0;
       end;
   end;

   if funre<>0 then
   begin
     boxnum := 0;
     AssignFile(filehandle, FileName);
     Reset(filehandle);

     fsize := filesize(filehandle);
     if fsize <> 224 then
        funre := 0
     else
     begin
       frmMain.caption := 'PUSH BOX  @Task:' + inttostr(taskID);
       for i := 1 to 14 do
         for j := 1 to 16 do
         begin
          read(filehandle, box[i][j]);
          if (box[i][j] = 57) or (box[i][j] = 58 ) then  //default animation is down
          begin
             currX := i;
             currY := j;
          end;
          if (box[i][j] = BOX_EMPTY) then
             boxnum := boxnum + 1;
        end;
       end;
       closefile(filehandle);
     end
  else
    taskID := 0;
    readTask := funre;
end;

procedure TfrmMain.mnuExitClick(Sender: TObject);
begin
   frmMain.close;
end;

procedure TfrmMain.FormCreate(Sender: TObject);
var
  reslut, i ,j: Integer;
begin
  tmrAnimate.enabled := false;
  for i := 1 to 14 do
   for j := 1 to 16 do
     box[i][j] := 0;

  //Jimmy: this will prevent from flashing.
  DoubleBuffered  :=  true;
  
  drawbkground();

  frmMain.width := 480;
  tmrAnimate.enabled := false;
  getdir(0, comdir);

  if comdir[strlen(pchar(comdir))] = '\' then
     comdir[strlen(pchar(comdir))] := ' ';

  comdir := trimright(comdir);
  lastX := 0;
  lastY := 0;
  currX := 0;
  currY := 0;
  taskID := 1;
  boxini := tinifile.Create(comdir + '\box.ini');
  taskID := boxini.Readinteger('score', 'lastscore', 0);
  soundflag := boxini.Readbool('sound', 'sound', true);
  mnuSound.checked := soundflag;

  reslut := readTask();

  if reslut = 0 then
  begin
    showmessage('No Task!');
    taskID := 0;
  end
  else
  begin
    drawbkground;
    frmMain.caption := 'Push Box @Task:' + inttostr(taskID);
    tmrAnimate.enabled := true;
  end;
end;

procedure tfrmMain.drawbkground;
var
  i, j: integer;
begin
  tmrAnimate.enabled := false;
  
  for i := 1 to 14  do
    for j := 1 to 16 do
      drawimage(i, j, box[i][j]);
  
  tmrAnimate.enabled := true;
end;

procedure TfrmMain.playSound(SoundType: Integer);
begin
  //if soundflag then
  //  sndplaysound(pchar(comdir + '\sound\move.wav'),SND_ASYNC);
end;        
     
procedure TfrmMain.formKeyDown(Sender: TObject; var Key: Word;
  Shift: TShiftState);
var
  bestmove, xflag, yflag: integer;
  UP, LEFT, RIGHT, DOWN: integer;
begin
  UP := 0; LEFT := 0; RIGHT:= 0; DOWN := 0;
  case key of
    VK_LEFT:
    begin
       xflag := 0;
       yflag := -1; //the coloum -1
       LEFT  := 1;
     end;

    VK_UP:
    begin
      xflag := -1; //the row -1
      yflag := 0;
      UP := 1;
    end;

    VK_RIGHT:
    begin
       xflag := 0;
       yflag := 1;
       RIGHT := 1;
    end;

    VK_DOWN:
    begin
      xflag := 1;
      yflag := 0;
      DOWN := 1;
    end;

  else
     playSound(SOUND_NOMOVE);
     exit;
  end;

  case box[currX + xflag][currY + yflag] of
  BG_FLOOR:
  begin
      playSound(SOUND_MOVE);
      
      box[currX + xflag][currY + yflag] := 50 + UP *1 + LEFT*3 + RIGHT*5 + DOWN*7;
      case box[currX][currY] of
        51,52,53,54,55,56,57,58: box[currX][currY] := BG_FLOOR;
        61,62,63,64,65,66,67,68: box[currX][currY] := BALL;
      end;
      boxmoved := false;
    end;
    
  BALL: //if next position is BALL, use different indicator.
  begin
    playSound(SOUND_MOVE);

    box[currX + xflag][currY + yflag] := 60 + UP *1 + LEFT*3 + RIGHT*5 + DOWN*7;    
    case box[currX][currY] of
       51,52,53,54,55,56,57,58: box[currX][currY] := BG_FLOOR;
       61,62,63,64,65,66,67,68: box[currX][currY] := BALL;
    end;
    boxmoved := false;
  end;
            
  BOX_EMPTY:
  begin
    //check whether the empty box can move.
    case box[currX + xflag + xflag][currY + yflag + yflag] of
       BG_FLOOR:
       begin
          playSound(SOUND_PUSHBOX);
           
          box[currX + xflag + xflag][currY + yflag + yflag] := BOX_EMPTY;
          box[currX + xflag][currY + yflag] := 50 + UP *1 + LEFT*3 + RIGHT*5 + DOWN*7;
          
          case box[currX][currY] of
             51,52,53,54,55,56,57,58:box[currX][currY] := BG_FLOOR;
             61,62,63,64,65,66,67,68:box[currX][currY] := BALL;
          end;
          boxmoved := true;
       end;
       
       BALL:
       begin
         playSound(SOUND_PUSHBOX);
         box[currX + xflag + xflag][currY + yflag + yflag] := BOX_FULL;
         box[currX + xflag][currY + yflag] := 50 + UP *1 + LEFT*3 + RIGHT*5 + DOWN*7;
         boxnum := boxnum-1;
         case box[currX][currY] of
           51,52,53,54,55,56,57,58: box[currX][currY] := BG_FLOOR;
           61,62,63,64,65,66,67,68: box[currX][currY] := BALL;
         end;
         boxmoved := true;
       end;
       else
       begin
          playSound(SOUND_NOMOVE);
          exit;
       end;
    end;
  end;     
            
  BOX_FULL:
  begin  //check whether the full box can move
    case box[currX + xflag + xflag][currY + yflag + yflag] of
    BG_FLOOR:
      begin
        playSound(SOUND_PUSHBOX);
     
        box[currX + xflag + xflag][currY + yflag + yflag] := BOX_EMPTY;
        box[currX + xflag][currY + yflag] := 60 + UP *1 + LEFT*3 + RIGHT*5 + DOWN*7;
        
        boxnum := boxnum + 1;
        case box[currX][currY] of
          51,52,53,54,55,56,57,58: box[currX][currY] := BG_FLOOR;
          61,62,63,64,65,66,67,68: box[currX][currY] := BALL;
        end;
        
        boxmoved := true;
      end;
    BALL:
      begin
        playSound(SOUND_PUSHBOX);
    
        box[currX + xflag + xflag][currY + yflag + yflag] := BOX_FULL;
        box[currX + xflag][currY + yflag] := 60 + UP *1 + LEFT*3 + RIGHT*5 + DOWN*7;
       
        case box[currX][currY] of
          51,52,53,54,55,56,57,58: box[currX][currY] := BG_FLOOR;
          61,62,63,64,65,66,67,68: box[currX][currY] := BALL;
        end;
        
        boxmoved := true;
      end;
    else
      begin
        playSound(SOUND_NOMOVE);
        exit;
      end;
    end;
  end;        
  else
  begin
     playSound(SOUND_NOMOVE);
     exit;
     end;
  end;
  
  lastX := currX;
  lastY := currY;
  currX := currX + xflag;
  currY := currY + yflag;

  tmrAnimate.enabled := false;
  steps := steps + 1;
  statusbar1.Panels[1].text := 'You have moved ' + inttostr(steps) + ' Steps';

  drawbkground;

  if (boxnum = 0) then  //task finished.
  begin
    playSound(SOUND_OVER);

    tmrAnimate.enabled := false;

    bestmove := boxini.readinteger('box','box' + inttostr(taskID),0);
    
    if (steps<bestmove) or (bestmove=0) then
    begin
       boxini.WriteInteger('box','box' + inttostr(taskID), steps);
       showmessage('Well done!');
    end
    else
       showmessage('You need more practice!');

    steps := 0;
    statusbar1.Panels[1].text := 'You have moved ' + inttostr(steps) + ' steps';
    taskID := taskID + 1;
    lastX := 0;
    lastY := 0;
    currX := 0;
    currY := 0;

    readTask; //Read New Tasks
    drawbkground;
  end;

  tmrAnimate.enabled := true;
end;

procedure TfrmMain.mnuRestartClick(Sender: TObject);
begin
  playSound(SOUND_RETRY);  
  steps := 0;
  statusbar1.panels[1].text := 'You have moved ' + inttostr(steps) + 'steps';
  lastX := 0;
  lastY := 0;
  currX := 0;
  currY := 0;
  readTask(); 
  drawbkground;
end;

procedure TfrmMain.mnuDesignClick(Sender: TObject);
var
  re: Integer;
begin
  caption := 'New design 14*16';
  re := frmDesign.showmodal;

  if (re=2) and (taskID=0) then
    begin
      taskID := taskID + 1;
      lastX := 0;
      lastY := 0;
      currX := 0;
      currY := 0;
      readTask();
      drawbkground();
      caption := 'Push Box Task @' + inttostr(taskID);
    end;
end;

procedure TfrmMain.mnuFunctionClick(Sender: TObject);
begin
  playSound(SOUND_CLICK);
  mnuUndo.enabled := false;
  mnuRestart.enabled := true;
  if lastX <> 0 then
    mnuUndo.enabled := true;
end;

procedure TfrmMain.mnuitDesignClick(Sender: TObject);
begin
  playSound(SOUND_CLICK);
end;

procedure TfrmMain.mnuUndoClick(Sender: TObject);
var
  i,j:integer;
begin
  playSound(SOUND_BACKBOX);
  Dec(steps);
  statusbar1.Panels[1].text := 'You have moved ' + inttostr(steps) + ' steps';

  case box[lastX][lastY] of
     BG_FLOOR: box[lastX][lastY] := 57;
     BALL: box[lastX][lastY] := 67;
  end;
  
  if boxmoved then
    case box[currX][currY] of
      51,52,53,54,55,56,57,58: box[currX][currY] := BOX_EMPTY;
      61,62,63,64,65,66,67,68: box[currX][currY] := BOX_FULL;
    end
  else
    case box[currX][currY] of
      51,52,53,54,55,56,57,58: box[currX][currY] := BG_FLOOR;
      61,62,63,64,65,66,67,68: box[currX][currY] := BALL;
    end;
    
  if (lastX=currX) and (lastY=currY-1) then  //down
  begin
    if boxmoved then
      case box[currX][currY + 1] of
        BOX_FULL:  box[currX][currY + 1] := BALL;
        BOX_EMPTY: box[currX][currY + 1] := BG_FLOOR;
      end;
  end;
  
  if (lastX = currX) and (lastY = currY + 1) then //up
  begin
    if boxmoved then
    begin
      case box[currX][currY-1] of
        BOX_FULL:  box[currX][currY-1] := BALL;
        BOX_EMPTY: box[currX][currY-1] := BG_FLOOR;
      end;
    end
  end;

  if (lastX = currX-1) and (lastY = currY) then //right
  begin
    if boxmoved then
      case box[currX + 1][currY] of
        BOX_FULL:  box[currX + 1][currY] := BALL;
        BOX_EMPTY: box[currX + 1][currY] := BG_FLOOR;
      end;
  end;
  
  if (lastX=currX + 1) and (lastY=currY) then  //left
  begin
    if boxmoved then
      case box[currX-1][currY] of
        BOX_FULL:  box[currX-1][currY] := BALL;
        BOX_EMPTY: box[currX-1][currY] := BG_FLOOR;
      end;
  end;

  currX := lastX;
  currY := lastY;
  lastX := 0;
  lastY := 0;
  boxnum := 0;

  for i := 1 to 16 do
    for j := 1 to 14 do
    begin
      if (box[i][j] = 3) then
         boxnum := boxnum + 1;
    end;
    
  drawbkground;
end;


procedure TfrmMain.FormClose(Sender: TObject; var Action: TCloseAction);
begin
  if boxini<>nil then
  begin
    boxini.WriteInteger('score','lastscore',taskID);
    boxini.writebool('sound','sound',soundflag);
    boxini.Destroy;
  end;
end;

procedure TfrmMain.tmrAnimateTimer(Sender: TObject);
var 
  spirit: TImage;
begin
  tmrAnimate.enabled := false;

  if (box[currX][currY] mod 2) = 0 then
     box[currX][currY] := box[currX][currY] - 1
  else
     box[currX][currY] := box[currX][currY] + 1;

  case box[currX][currY] of
    51,61: spirit := imgPushU1;
    52,62: spirit := imgPushU2;
    53,63: spirit := imgPushL1;
    54,64: spirit := imgPushL2;
    55,65: spirit := imgPushR1;
    56,66: spirit := imgPushR2;
    57,67: spirit := imgPushD1;
    58,68: spirit := imgPushD2;
    else
      exit;
  end;
  
  bkground.Canvas.CopyRect(rect(currY*30-30, currX*30-30, currY*30, currX*30),
                           spirit.Canvas,
                           rect(0, 0, 30, 30));
  tmrAnimate.enabled := true;
end;


procedure TfrmMain.mnuHelpClick(Sender: TObject);
begin
  shellexecute(application.handle,pchar('open'),pchar('winhlp32.exe'),pchar('box.hlp'),pchar(comdir),sw_show);
end;

procedure TfrmMain.mnuAboutClick(Sender: TObject);
begin
  frmAbout.showmodal;
end;

procedure TfrmMain.mnuSoundClick(Sender: TObject);
begin
 soundflag := not soundflag;
 if soundflag then
    mnuSound.checked := true
 else
    mnuSound.checked := false;
end;

end.



