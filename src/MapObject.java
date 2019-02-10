import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class MapObject {
	int x,y,width,height;
	String textureName;
	Image image;
	public MapObject(int x, int y, String textureName){
		this.x=x;
		this.y=y;
		this.textureName=textureName;
		try{
			this.image=ImageIO.read(new File("./mapObjects/"+textureName+".png"));
		}
		catch(Exception ex){
			System.out.println("Не удалось найти изображение объекта "+textureName);
		}
		this.width=image.getWidth(null);
		this.height=image.getHeight(null);
	}
	
}
