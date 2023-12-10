public class ksiazka {

        //private final Object lock=new Object();
        String txt="aaa";

        public void write(String txttowrite) {
            //synchronized (lock) {
            txt = "";
            for (int a=0;a<txttowrite.length();a++){
                    txt=txt+txttowrite.charAt(a);
                    try {
                            Thread.sleep(1000);
                    } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                    }
            }
            //}
        }
        public String read(){
            //synchronized (lock){
                    return txt;
            //}
        }

}


