package com.example.dean.ibuytogether;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.nio.ByteBuffer.allocate;

public class PushActivity extends Activity implements View.OnClickListener {
    EditText username, password, cont;
    Editable un, pw, ct;
    private String host = "ptt.cc";
    private int port = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push);
        host = "ptt.cc";
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        cont = (EditText) findViewById(R.id.cont);
        un = username.getEditableText();
        pw = password.getEditableText();
        cont.setText("推，已填G單");
        ct = cont.getEditableText();
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

    }


    @Override
    public void onClick(View arg0) {
        TelnetClientNio push = new TelnetClientNio(host,port);
        push.start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msag) {
            super.handleMessage(msag);
            Toast.makeText(PushActivity.this,msag.getData().getString("push"),Toast.LENGTH_LONG).show();
        }
    };

    class TelnetClientNio extends Thread {
        private String host;
        private int port;
        private SocketChannel channel;
        public static final int IAC = 255;
        public static final int WILL = 251;
        public static final int WONT = 252;
        public static final int DO = 253;
        public static final int DONT = 254;
        private int isPush = 0;

        public TelnetClientNio(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void run() {
            TelnetClientNio client = new TelnetClientNio(host, port);
            client.mconnect();
            PushActivity.this.finish();
        }

        public void mconnect() {
            try {
                InetSocketAddress addr = new InetSocketAddress(host, port);
                try {
                    channel = SocketChannel.open(addr);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteBuffer buf = allocate(24000);
                while (true) {
                    buf.clear();
                    channel.read(buf);
                    System.out.println("(緩衝區內的有效資料個數:" + buf.position() + ")");
                    if (buf.position() == 0)
                        break;
                    buf.flip();
                    String msg = "";
                    while (buf.hasRemaining()) {
                        byte b = buf.get();
                        int data = b & 0xFF; // 將byte轉為整數
                        if (data == IAC) {
                            handleCommand(buf);
                        } else {
                            char c = getBig5Char(data, buf);
                            msg += c;
                        }
                    }
                    msg = msg.trim();
                    if (msg.length() > 0) {
                        System.out.println(msg);
                        if (isPush == 0) {
                            writeMessage(msg);
                        }
                    }
				/* 如何切斷連線？ */
                    if(isPush==1)
                    {break;}
                }
                //channel.close();
                //System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        public void writeMessage(String msg) throws IOException {
            int condition = 0;
            ByteBuffer outBuf = allocate(1024);
            outBuf.clear();
            int cr = 13;
            if (msg.contains("輸入代號") == true) {
                condition = 1; //login
            } else if (msg.contains("請輸入您的密碼") == true) {
                condition = 2; //passward
            } else if (msg.contains("重複登入") == true) {
                condition = 3;
            } else if (msg.contains("按任意鍵") == true || msg.contains("歡迎大家") == true) {
                condition = 4;
            } else if (msg.contains("主功能表") == true) {
                condition = 5;
            } else if (msg.contains("選擇看板") == true) {
                condition = 6;
            } else if (msg.contains("文章選讀") == true) {
                condition = 7;
            } else if (msg.contains("瀏覽") == true) {
                condition = 8;
            }
            switch (condition) {
                case 1: //userid
                    outBuf.put(un.toString().getBytes());
                    outBuf.put((byte) cr);
                    System.out.println("userid");
                    break;
                case 2: //pw
                    outBuf.put(pw.toString().getBytes());
                    outBuf.put((byte) cr);
                    System.out.println("pw");
                    break;
                case 3: //repeat login
                    outBuf.put("n".getBytes());
                    outBuf.put((byte) cr);
                    System.out.println("repeat");
                    break;
                case 4: //any key
                    outBuf.put((byte) cr);
                    System.out.println("anykey");
                    break;
                case 5: //main list
                    outBuf.put("f".getBytes());
                    outBuf.put((byte) cr);
                    System.out.println("mainlist");
                    break;
                case 6: //board list
                    outBuf.put("4".getBytes());
                    outBuf.put((byte) cr);
                    outBuf.put((byte) cr);
                    System.out.println("favorate board list");
                    break;
                case 7: //article list
                    outBuf.put("255".getBytes());
                    outBuf.put((byte) cr);
                    outBuf.put((byte) cr);
                    System.out.println("article inside~");
                    break;
                case 8: //
                    outBuf.put("X".getBytes());
                    outBuf.put("1".getBytes());
                    //String s = "推";
                    //String strReturn = new String("推".getBytes("UTF-8"), "big5");
                    outBuf.put(ct.toString().getBytes("big5"));
                    outBuf.put((byte) cr);
                    outBuf.put("y".getBytes());
                    outBuf.put((byte) cr);
                    isPush = 1;
                    Bundle pBundle = new Bundle();
                    String ps = "推文成功";
                    pBundle.putString("push",ps);

                    Message hmsg = new Message();
                    hmsg.setData(pBundle);

                    mHandler.sendMessage(hmsg);
                    System.out.println("PUSHPUSH: "+isPush);
                    break;
                //default:
                //outBuf.put((byte)cr);
                //  break;
            }

            outBuf.flip();
            channel.write(outBuf);
        }

        public void handleCommand(ByteBuffer buf) throws IOException {
            ByteBuffer outBuf = allocate(1024);
            int tone = buf.get() & 0xFF;
            int option = buf.get() & 0xFF;
            outBuf.clear();
            if (tone == DO) {
                outBuf.put((byte) IAC);
                outBuf.put((byte) WONT);
                outBuf.put((byte) option);
                outBuf.flip();
                channel.write(outBuf);
            } else if (tone == WILL) {
                outBuf.put((byte) IAC);
                outBuf.put((byte) DONT);
                outBuf.put((byte) option);
                outBuf.flip();
                channel.write(outBuf);
            }
        }

        public char getBig5Char(int data, ByteBuffer buf)
                throws IOException {
            char c = (char) data;
            if (data > 127) {
                byte[] big5 = new byte[2];
                big5[0] = (byte) data;
                big5[1] = (byte) buf.get();
                c = new String(big5, "BIG5").charAt(0);
            }
            return c;
        }
    }
}