package bombe2.core;

import bombe2.annotations.MethodVisibility;
import bombe2.core.data.EventObject;
import bombe2.core.data.ReturnableObject;
import bombe2.core.definitions.Propagator;
import bombe2.distributed.RootManager;
import bombe2.exceptions.PropagationException;

import java.lang.reflect.Method;

//TODO definire una struttura più elegante
public abstract class AbstractService implements Propagator {
    private final Entity entity;
    private Propagator propagator;
    private String path;
    public AbstractService(String name){
        entity = new Entity(name);
    }

    public Propagator getPropagator() {
        return propagator;
    }

    public Entity getEntity() {
        return entity;
    }

    //region events
    protected void onManagerAdded(){}
    protected void onCreate() {}
    protected void onDestroy(){}
    protected void onStart(){}
    protected void onStop(){}
    protected void onPause(){}
    protected void onResume(){}
    protected void onRestart(){}

    //endregion
    @Deprecated
    public AbstractService searchServiceRoot(String name){
        return RootManager.
                getInstance().
                getManager().
                getService(name);
    }

    @Override
    public String toString() {
        return "AbstractService{" +
                "entity=" + entity +
                ", eventPropagator=" + propagator +
                '}';
    }

    protected String calcPath(){
        assert propagator != null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(((Manager)propagator).forwardPathRequest());
        if (!stringBuilder.toString().equals(""))
            stringBuilder.append(".");
        return stringBuilder.append(entity.getName()).toString();
    }

    public String getPath() {
        return path;
    }

    protected final ReturnableObject<?> propagateInside(EventObject eventObject)
            throws ReflectiveOperationException {
        if (eventObject.hasNext())
            throw new PropagationException(
                    "end services can't route = "+
                            eventObject.getCoordinate()
            );

        ReturnableObject<?> returnableObject;

        Method method =
                this.getClass()
                        .getMethod(eventObject.getMethod(), eventObject.getTypes());

        MethodVisibility methodVisibility;
        if ((methodVisibility = method.getAnnotation(MethodVisibility.class)) == null)
            throw new PropagationException("Method visibility not defined at " + getPath());
        if (!EventObject.visibilityTest(eventObject.getOrigin(),methodVisibility.visibility()))
            throw new PropagationException(
                    "You can not invoke "
                            + getPath() + ":" + eventObject.getMethod() +
                            ", with visibility type = "+
                            methodVisibility.visibility()+
                            ", from "+
                            eventObject.getOrigin()
            );


        returnableObject = (ReturnableObject<?>) method.invoke(this, eventObject.getParams());
        return returnableObject;
    }
}
