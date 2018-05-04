package com.mygdx.game.Bott;

import com.mygdx.game.WObjects.Map;

import java.util.List;

public class Bot {



    public Bot(Map map){

        BotMap botMap = new BotMap(map);
        dfs(botMap);
    }


    private void dfs(BotMap botMap) {
        PathFinder pathFinder = new PathFinder();
        List<Coordinate> path = pathFinder.solve(botMap);
        for(int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getStringX() + " : "+path.get(i).getStringY());
        }

        botMap.printPath(path);
        botMap.reset();
    }
}
