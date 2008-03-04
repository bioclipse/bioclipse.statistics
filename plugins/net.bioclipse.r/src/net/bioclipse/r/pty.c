/******************************************************
 * PTY wrapper needed for R                           *
 * By Bjarni Juliusson <bjarni@update.uu.se>          *
 * Compile for each platform as pty-os-arch, example: *
 *   pty-linux-i386                                   *
 * Name parts should match system properties          *
 * "os.name" and "os.arch" converted to lowercase.    *
 * These binaries go in native-bin.                   *
 ******************************************************/

#define _XOPEN_SOURCE
#include<errno.h>
#include<string.h>
#include<stdlib.h>
#include<stdio.h>
#include<fcntl.h>
#include<termios.h>
#include<unistd.h>
#include<sys/time.h>
#include<sys/types.h>


int main(){
  int ptm;
  char *pts;
  int pid;
  int ptsr, ptsw;
  char c;
  struct termios oldterm, term;
  fd_set fds;
  int t;

  if(setsid()==-1){  /* Happens if we are group leader */
    if(!fork())setsid();
    else _exit(0);
    }

  if(((ptm=posix_openpt(O_RDWR))==-1)||grantpt(ptm)||unlockpt(ptm)){
    fprintf(stderr, "ptm: %s\n", strerror(errno));
    return 1;
    }

  if(!(pts=ptsname(ptm))){
    fprintf(stderr, "ptsname: %s\n", strerror(errno));
    return 1;
    }

  if(((ptsr=open(pts, O_RDONLY))==-1)||((ptsw=open(pts, O_WRONLY))==-1)){
    fprintf(stderr, "open: %s\n", strerror(errno));
    return 1;
    }

  if((pid=fork())==-1){
    fprintf(stderr, "fork: %s\n", strerror(errno));
    return 1;
    }

  if(!pid){
    close(0); close(1); close(2);
    dup2(ptsr, 0);
    dup2(ptsw, 1); dup2(ptsw, 2);

    tcgetattr(0, &term);
    term.c_lflag&=~(ECHO|ECHONL);
    tcsetattr(0, TCSANOW, &term);

    execlp(
      "R", "--vanilla", "--slave", "--no-save", "--no-restore-data", (char *)0
      );
    }
  else{
    tcgetattr(0, &oldterm);
    term=oldterm;
    term.c_iflag&=~(IGNBRK|BRKINT|PARMRK|ISTRIP|INLCR|IGNCR|ICRNL|IXON);
    term.c_oflag&=~OPOST;
    term.c_lflag&=~(ECHO|ECHONL|ICANON|ISIG|IEXTEN);
    term.c_cflag&=~(CSIZE|PARENB);
    term.c_cflag|=CS8;
    tcsetattr(0, TCSANOW, &term);

    while(1){
      FD_ZERO(&fds);
      FD_SET(0, &fds);
      FD_SET(ptm, &fds);
      select(ptm+1, &fds, 0, 0, 0);
      if(FD_ISSET(0, &fds)){
        if(read(0, &c, 1)<1)break;
        write(ptm, &c, 1);
        }
      if(FD_ISSET(ptm, &fds)){
        if(read(ptm, &c, 1)<1)break;
        write(1, &c, 1);
        }
      }

    tcsetattr(0, TCSANOW, &oldterm);
    }

  return 0;
  }
