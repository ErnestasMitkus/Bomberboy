package com.draznel.bomberboy.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.draznel.bomberboy.Main;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Main.WIDTH, Main.HEIGHT);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new Main();
        }
}