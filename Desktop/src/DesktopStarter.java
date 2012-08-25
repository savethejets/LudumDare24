import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ludumdare.evolution.LudumDareMain;
import com.ludumdare.evolution.app.Constants;

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

        cfg.title = Constants.MAIN_WINDOW_TITLE;
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;

        LwjglApplication application = new LwjglApplication(new LudumDareMain(), cfg);

    }
}