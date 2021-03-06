package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.mygdx.game.GameObjects.LevelInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class LevelParser
{
    static SaveFile savefile;
    static int LevelNumber;

    static Hashtable<Integer, Point> bulbsTarget = new Hashtable<Integer, Point>();
    static Hashtable<Integer, Point> bulbsSource = new Hashtable<Integer, Point>();

    public static void ParseTexture(Texture leveltex, SaveFile _savefile)
    {
        savefile = _savefile;
        TextureData data = leveltex.getTextureData();
        data.prepare();
        Pixmap map = data.consumePixmap();


        Color color = new Color();
        List<Point> Starts = new ArrayList<Point>();
        Hashtable<Point,Point> TriggerTarget = new Hashtable<Point, Point>();

        int[][] Map = new int[map.getWidth()][map.getHeight()];

        for (int x=0; x<map.getWidth(); x++)
            for (int y=0; y<map.getHeight(); y++) {
                int temp = map.getPixel(x, y);
                color.set(temp);
                String col = color.toString();
                float cola = color.a;

                if (col.equals("1279afff")) {
                    Starts.add(new Point(x,y));
                    Map[x][map.getHeight() - y - 1] = 1;}

                else if (col.equals("1279aeff"))
                { Map[x][map.getHeight() - y - 1] = 1;}

                else if (col.equals("ec9b00ff"))
                { Map[x][map.getHeight() - y - 1] = 2;}

                else if (col.equals("bede2cff"))
                { Map[x][map.getHeight() - y - 1] = 3;}

                else if (cola == 0.2f)
                {
                    int r = (int)(color.r*255);
                    if (r != 127)
                    bulbsTarget.put(r, new Point(x, map.getHeight() - y - 1));
                }

                else if (cola == 0.8f)
                {
                    int r = (int)(color.r*255);
                    int g = (int)(color.g*255);
                    int b = (int)(color.b*255);
                    bulbsSource.put(r, new Point(x, map.getHeight() - y - 1));
                    Point prime = new Point(x,map.getHeight() - y - 1);
                    Point target = new Point(g,map.getHeight() - b - 1);
                    TriggerTarget.put(prime, target);
                    Map[x][map.getHeight() - y - 1] = 1;
                }

                else{
                    Map[x][map.getHeight() - y - 1] = 0;
                }
            }
        LevelInfo LvlInfo = new LevelInfo(Map, Starts, TriggerTarget, Bulbs());

        bulbsSource = new Hashtable<Integer, Point>();
        bulbsTarget = new Hashtable<Integer, Point>();
        savefile.SaveLevel(LvlInfo, LevelNumber);
    }

    static Hashtable<Point, Point> Bulbs()
    {
        Hashtable<Point, Point> bulbs = new Hashtable<Point, Point>();
        for (Integer key : bulbsSource.keySet())
        {
            if (bulbsTarget.containsKey(key))
            bulbs.put(bulbsSource.get(key), bulbsTarget.get(key));
        }
        return bulbs;
    }

    public static void ParseNextLevel(int levelNumber, SaveFile sf)
    {
        LevelNumber = levelNumber;
        Texture level = new Texture("Level-"+LevelNumber+".png");
        LevelParser.ParseTexture(level, sf);
    }
}
