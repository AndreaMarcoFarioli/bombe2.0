package program;

import bombe.core.ExtendableService;
import bombe.core.data.EventObject;
import bombe.distributedArchitecture.MainManager;

public class ServizioA extends ExtendableService {
    public ServizioA() {
        super("servizioa", new Model());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        long t1 = System.currentTimeMillis();
        try {
            System.out.println(MainManager.getInstance().sendOver(new EventObject("ciccio:l", 1,2)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();

        System.out.println(t2-t1 + "ms");
    }
}
