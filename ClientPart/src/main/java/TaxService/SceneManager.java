package TaxService;

import javafx.stage.Stage;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

/**
 * Содержит стек scenes - история сцен. Благодаря ему из какой-либо сцены можно вернуться на главную.
 */
public class SceneManager
{
    private Stage stage;
    private Stack<ManagedScene> scenes;

    public SceneManager(Stage stage)
    {
        this.stage = stage;
        scenes = new Stack<ManagedScene>();
    }

    /** Помещает новую сцену в стек и переключает на неё stage
     * @param scene - Новая сцена.
     */
    public void pushScene(ManagedScene scene)
    {
        scenes.push(scene);
        //double w = stage.getWidth(), h = stage.getHeight();
        stage.setScene(scene);
        //stage.setWidth(w);
        //stage.setHeight(h);
    }

    /** Извлекает последнюю сцнеу из стека и переключает stage на предыдущую. Если последняя сцена была единственной, закрывает stage.
     * @throws InvocationTargetException если стек был пуст.
     */
    public void popScene() throws InvocationTargetException
    {
        if (scenes.empty())
            throw new InvocationTargetException(new Exception("Стек сцен пуст!"), "Стек сцен пуст!");
        scenes.pop();
        if (scenes.empty())
            stage.close();
        else
        {
            double w = stage.getWidth(), h = stage.getHeight();
            stage.setScene(scenes.peek());
            stage.setWidth(w);
            stage.setHeight(h);
        }
    }

    /** Извлекает из стека все сцены, кроме первой (которая была установлена в конструкторе) и переключает на неё stage.
     * @throws InvocationTargetException если стек был пуст.
     */
    public void popAllExceptFirst() throws InvocationTargetException
    {
        if (scenes.empty())
            throw new InvocationTargetException(new Exception("Стек сцен пуст!"), "Стек сцен пуст!");
        double w = stage.getWidth(), h = stage.getHeight();
        while (scenes.size() > 1)
            scenes.pop();
        stage.setScene(scenes.peek());
        stage.setWidth(w);
        stage.setHeight(h);
    }

    public ManagedScene getAt(int index)
    {
        return scenes.get(index);
    }

    public int getStackSize()
    {
        return scenes.size();
    }

    public ManagedScene getCurrScene()
    {
        return scenes.peek();
    }

    public Stage getStage()
    {
        return stage;
    }
}