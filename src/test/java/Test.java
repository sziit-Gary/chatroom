import javax.swing.filechooser.FileSystemView;
import java.io.DataInputStream;
import java.time.format.DateTimeFormatter;

/**
 * @author zhupengcheng
 * @date 2020/6/19 15:53
 */
public class Test {

    public static void main(String[] args) {
//        System.out.println(SwingUtils.getScreenWidth() * 0.5 + "\t" + SwingUtils.getScreenHeight() * 1);
        System.out.println(FileSystemView.getFileSystemView().getHomeDirectory() + "/");

    }
}
