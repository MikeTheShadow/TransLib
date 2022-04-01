# Trans Lib

## A multi-purpose translation library

> Video walkthrough of the code is here if that's more your style https://www.youtube.com/watch?v=UvhGFYaNFPE

# Use Case #1

## Your plugin needs to be globally configurable to different languages

Let's say you have a static configuration file called english.yml.
You also have a file called french.yml, german.yml etc.
These aren't necessarily updated by the end user themselves rather
you supply them as a sort of "language pack". To use this plugin along
with configurations like this the setup is as follows.

```java

import com.miketheshadow.translib.TransLib;

class LangConfig {

    TransLib transLib;

    public LangConfig(Plugin plugin) {
        //Folder containing the configuration files. (This assumes the data folder exists)
        File file = new File(plugin.getDataFolder(), "translations");
        
        //Create our TransLib instance. You can store this globally or wrap it in a class like this    
        this.transLib = new TransLib("english.yml", file);
    }
    
    public String getTranslation(String key) {
        return transLib.getTranslation(key);
    }
    
    public void setConfigUsed(String fileName) {
        transLib.setActiveConfig(fileName);
    }

}
```

In your main configuration you add a variable for language and the end user can
set the name of the config to whatever they like. You can also allow the user
to create their own lang configs for themselves and then in your config set the
name to whatever they like.

# Use Case #2

## (UNTESTED) per user language translations

Note: I have not tested this on a live server but there is no reason it should not work.

When the user joins you create a default instance of the config. (Or store the config name see below)

```java
TransLib transLib = new TransLib("english.yml", file);
```

Then you store it in a HashMap. You can store an entire TransLib class or just the xyz.yml string

```java
HashMap<UUID,String> userTransLibs = new HashMap<>();
```

If you have a per-player instance
```java
userTransLibs.get(player.getUniqueId()).getTranslation(key);
```

Otherwise, use (you can wrap the TransLib to use getTrans(player,key))

```without the instance
globalTransLib.getTranslationFromConfig(map.get(player.getUniqueId()),key);
```

