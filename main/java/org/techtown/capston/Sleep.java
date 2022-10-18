package org.techtown.capston;

public class Sleep {
    /*쓰레드로 강제 시간 멈춤*/
    public static void Sleep(){
        try {
            Thread.sleep(500);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
//nano
