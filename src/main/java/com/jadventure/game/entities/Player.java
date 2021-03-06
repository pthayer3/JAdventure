package com.jadventure.game.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.jadventure.game.items.Item;
import com.jadventure.game.items.ItemStack;
import com.jadventure.game.items.Backpack;
import com.jadventure.game.items.Storage;
import com.jadventure.game.classes.Recruit;
import com.jadventure.game.classes.SewerRat;
import com.jadventure.game.navigation.Coordinate;
import com.jadventure.game.navigation.ILocation;
import com.jadventure.game.navigation.LocationManager;
import com.jadventure.game.navigation.LocationType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

/*
 * This class deals with the player and all of its properties.
 * Any method that changes a character or interacts with it should
 * be placed within this class. If a method deals with entities in general or
 * with variables not unique to the player, place it in the entity class.
 */
public class Player extends Entity {
    private ILocation location;
    
    public Player(){
        
    }

    protected static String getProfileFileName(String name) {
        return "json/profiles/" + name + "/" + name + "_profile.json";
    }
    public static boolean profileExists(String name) {
        File file = new File(getProfileFileName(name));
        return file.exists();
    }

    public static Player load(String name) {
        Player player = new Player();

        JsonParser parser = new JsonParser();
        String fileName = getProfileFileName(name);
        try {
            Reader reader = new FileReader(fileName);
            JsonObject json = parser.parse(reader).getAsJsonObject();

            player.setName(json.get("name").getAsString());
            player.setHealthMax(json.get("healthMax").getAsInt());
            player.setHealth(json.get("health").getAsInt());
            player.setArmour(json.get("armour").getAsInt());
            player.setDamage(json.get("damage").getAsInt());
            player.setLevel(json.get("level").getAsInt());
            player.setStrength(json.get("strength").getAsInt());
            player.setIntelligence(json.get("intelligence").getAsInt());
            player.setDexterity(json.get("dexterity").getAsInt());
            player.setLuck(json.get("luck").getAsInt());
            player.setStealth(json.get("stealth").getAsInt());
            player.setWeapon(json.get("weapon").getAsString());
            player.setHead(json.get("head").getAsString());
            player.setChest(json.get("chest").getAsString());
            player.setLegs(json.get("legs").getAsString());
            player.setArms(json.get("arms").getAsString());
            player.setFeet(json.get("feet").getAsString());
            player.setShield(json.get("shield").getAsString());
            if (json.has("items")) {
                HashMap<String, Integer> items = new Gson().fromJson(json.get("items"), new TypeToken<HashMap<String, Integer>>(){}.getType());
                ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
                for (Map.Entry<String, Integer> entry : items.entrySet()) {
                    String itemID = entry.getKey();
                    int amount = entry.getValue();
                    Item item = new Item(itemID);
                    ItemStack itemStack = new ItemStack(amount, item);
                    itemList.add(itemStack);
                }
                float maxWeight = (float)Math.sqrt(player.getStrength()*300);
                player.setStorage(new Backpack(maxWeight, itemList));
            }
            Path orig = Paths.get("json/profiles/"+name+"/locations.json");
            Path dest = Paths.get("json/locations.json");
            Files.copy(orig, dest, StandardCopyOption.REPLACE_EXISTING);
            Coordinate coordinate = new Coordinate(json.get("location").getAsString());
            player.setLocation(LocationManager.getLocation(coordinate));
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println( "Unable to open file '" + fileName + "'.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return player;
    }

    // This is known as the singleton pattern. It allows for only 1 instance of a player.
    private static Player player;
    
    public static Player getInstance(String playerClass){ 
        if(playerClass.equals("recruit")){
            // Instead of having a huge constructor, this is much more readable.
            player = new Recruit();
            setUpVariables(player);
            return player;
            
        } else if(playerClass.equals("sewerrat")) {
            player = new SewerRat();
            setUpVariables(player);
            return player;
        }
        return player;
    }

    public static void setUpVariables(Player player) {
        player.setLocation(LocationManager.getInitialLocation());
        float maxWeight = (float)Math.sqrt(player.getStrength()*300);
        player.setStorage(new Backpack(maxWeight));
        player.addItemToStorage(new Item("fmil1"));
    }

    public void getStats(){
        Item weapon = new Item(getWeapon());
        String weapname = weapon.getName();

        if (weapname == null){
            weapname = "hands";
        }
        
        Item head = new Item(getHead());
        String headname = head.getName();

        if (headname == null){
            headname = "none";
        }
        Item chest = new Item(getChest());
        String chestname = chest.getName();

        if (chestname == null){
            chestname = "shirt";
        }

        Item arms = new Item(getArms());
        String armname = arms.getName();

        if (armname == null){
            armname = "none";
        }

        Item legs = new Item(getLegs());
        String legname = legs.getName();

        if (legname == null){
            legname = "pants";
        }

        Item feet = new Item(getFeet());
        String feetname = feet.getName();

        if (feetname == null){
            feetname = "none";
        }

        Item shield = new Item(getShield());
        String shieldname = shield.getName();

        if (shieldname == null){
            shieldname = "none";
        }
  
        System.out.println("\nPlayer name: " + getName() +
                            "\nCurrent weapon: " + weapname +
                             "\nCurrent armour: Head- " + headname + " Chest- " + chestname + " Arms- " + armname + " Legs- " + legname + " Feet- " + feetname + " Shield- " + shieldname + 
                            "\nGold: " + getGold() +
                            "\nHealth/Max: " + getHealth() + "/" + getHealthMax() +
                            "\nDamage/Armour: " + getDamage() + "/" + getArmour() +
                            "\nStrength: " + getStrength() +
                            "\nIntelligence: " + getIntelligence() +
                            "\nDexterity: " + getDexterity() +
                            "\nLuck: " + getLuck() +
                            "\nStealth: " + getStealth() +
                            "\n" + getName() + "'s level: " + getLevel());
    }

    public void printBackPack() {
        this.storage.display();
    }

    public void save() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", getName());
        jsonObject.addProperty("healthMax", getHealthMax());
        jsonObject.addProperty("health", getHealthMax());
        jsonObject.addProperty("armour", getArmour());
        jsonObject.addProperty("damage", getDamage());
        jsonObject.addProperty("level", getLevel());
        jsonObject.addProperty("strength", getStrength());
        jsonObject.addProperty("intelligence", getIntelligence());
        jsonObject.addProperty("dexterity", getDexterity());
        jsonObject.addProperty("luck", getLuck());
        jsonObject.addProperty("stealth", getStealth());
        jsonObject.addProperty("weapon", getWeapon());
        jsonObject.addProperty("head", getHead());
        jsonObject.addProperty("chest", getChest());
        jsonObject.addProperty("legs", getLegs());
        jsonObject.addProperty("arms", getArms());
        jsonObject.addProperty("feet", getFeet());
        jsonObject.addProperty("shield", getShield());
        HashMap<String, Integer> items = new HashMap<String, Integer>();
        JsonArray itemList = new JsonArray();
        for (ItemStack item : getStorage().getItems()) {
            items.put(item.getItem().getItemID(), item.getAmount());
            JsonPrimitive itemJson = new JsonPrimitive(item.getItem().getItemID());
            itemList.add(itemJson);
        }
        JsonElement itemsJsonObj = gson.toJsonTree(items);
        jsonObject.add("items", itemsJsonObj);
        Coordinate coordinate = getLocation().getCoordinate();
        String coordinateLocation = coordinate.x+","+coordinate.y+","+coordinate.z;
        jsonObject.addProperty("location", coordinateLocation);

        String fileName = getProfileFileName(getName());
        new File(fileName).getParentFile().mkdirs();
        try {
            Writer writer = new FileWriter(fileName);
            gson.toJson(jsonObject, writer);
            writer.close();
            LocationManager.writeLocations();
            Path orig = Paths.get("json/locations.json");
            Path dest = Paths.get("json/profiles/"+getName()+"/locations.json");
            Files.copy(orig, dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("\nYour game data was saved.");
        } catch (IOException ex) {
            System.out.println("\nUnable to save to file '" + fileName + "'.");
        }
    }

    public ArrayList<Item> searchItem(String itemName, ArrayList<Item> itemList) {
        ArrayList<Item> itemMap = new ArrayList<Item>();
        for (Item item : itemList) {
            String testItemName = item.getName();
            if (testItemName.equals(itemName)) {
                itemMap.add(item);
            }
        }
        return itemMap;
    }

    public ArrayList<Item> searchItem(String itemName, Storage storage) {
        ArrayList<Item> itemMap = new ArrayList<Item>();
        for (ItemStack item : storage.getItems()) {
            String testItemName = item.getItem().getName();
            if (testItemName.equals(itemName)) {
                itemMap.add(item.getItem());
            }
        }
        return itemMap;
    }

    public void pickUpItem(String itemName) {
        ArrayList<Item> itemMap = searchItem(itemName, getLocation().getItems());
        if (!itemMap.isEmpty()) {
            Item item = itemMap.get(0);
            Item itemToPickUp = new Item(item.getItemID());
            addItemToStorage(itemToPickUp);
            location.removePublicItem(itemToPickUp.getItemID());
            System.out.println("\n" + item.getName()+ " picked up");
        }
    }

    public void dropItem(String itemName) {
        ArrayList<Item> itemMap = searchItem(itemName, getStorage());
        if (!itemMap.isEmpty()) {
            Item item = itemMap.get(0);
            Item itemToDrop = new Item(item.getItemID());
            Item weapon = new Item(getWeapon());
            String wName = weapon.getName();
            Item head = new Item(getHead());
            String hName = head.getName();
            Item chest = new Item(getChest());
            String cName = chest.getName();
            Item legs = new Item(getLegs());
            String lName = legs.getName();
            Item arms = new Item(getArms());
            String aName = arms.getName();
            Item feet = new Item(getFeet());
            String fName = feet.getName();
            Item shield = new Item(getShield());
            String sName = shield.getName();


            if (itemName.equals(wName)) {
                dequipItem(wName);
            }
            if (itemName.equals(hName)) {
                dequipItem(hName);
            }
            if (itemName.equals(cName)) {
                dequipItem(cName);
            }
            if (itemName.equals(lName)) {
                dequipItem(lName);
            }
            if (itemName.equals(aName)) {
                dequipItem(aName);
            }
            if (itemName.equals(fName)) {
                dequipItem(fName);
            }
            if (itemName.equals(sName)) {
                dequipItem(sName);
            }
            removeItemFromStorage(itemToDrop);
            location.addPublicItem(itemToDrop.getItemID());
            System.out.println("\n" + item.getName()+ " dropped");
        }
    }
    
    public enum Part {
	onehand, twohand, head, chest, legs, arms, feet, shield;
    }

    public void equipItem(String itemName) {
        ArrayList<Item> itemMap = searchItem(itemName, getStorage());
        if (!itemMap.isEmpty()) {
            String name;
            Item item = itemMap.get(0);
            String bodypart = item.getBodypart();
            Part bpart = Part.valueOf(bodypart.toLowerCase());
            switch (bpart) {

            case onehand: 
                Item pweapon = new Item(getWeapon());
                name = pweapon.getName();

                   if (name == null){
                     name = "hands";
                   }  
                   
                   if (name.equals("hands")){
                   setWeapon(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
                   
                   else {
                   dequipItem(name);
                   setWeapon(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
                  break;

            case twohand: 
                pweapon = new Item(getWeapon());
                name = pweapon.getName();

                if (name == null){
                    name = "hands";
                   }
                   
                   if (name.equals("hands")){
                   setWeapon(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   Item tempshield = new Item(getShield());
                   String sname = tempshield.getName();
                   dequipItem(sname);
                   }
                   
                   else {
                   dequipItem(name);
                   setWeapon(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   Item tempshield = new Item(getShield());
                   String sname = tempshield.getName();
                   dequipItem(sname);
                   System.out.println("\n" + item.getName()+" dequipped");
                   }

                  break;
            case head: 
                Item phead = new Item(getHead());
                name = phead.getName();

                   if (name == null){
                     name = "none";
                   }  
				   
                   if (name.equals("none")){
                   setHead(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
				   
                   else {
                   dequipItem(name);
                   setHead(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
                  break;

            case chest: 
                Item pchest = new Item(getChest());
                name = pchest.getName();

                   if (name == null){
                     name = "shirt";
                   }  
				   
                   if (name.equals("shirt")){
                   setChest(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
				   
                   else {
                   dequipItem(name);
                   setChest(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }

                  break;
            case legs: 
                Item plegs = new Item(getLegs());
                name = plegs.getName();

                   if (name == null){
                     name = "pants";
                   }  
				   
                   if (name.equals("pants")){
                   setLegs(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
				   
                   else {
                   dequipItem(name);
                   setLegs(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }

                  break;
            case arms: 
                Item parms = new Item(getArms());
                name = parms.getName();

                   if (name == null){
                     name = "none";
                   }  
				   
                   if (name.equals("none")){
                   setArms(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
				   
                   else {
                   dequipItem(name);
                   setArms(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }

                  break;
            case feet: 
                Item pfeet = new Item(getFeet());
                name = pfeet.getName();

                   if (name == null){
                     name = "none";
                   }  
				   
                   if (name.equals("none")){
                   setFeet(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }
				   
                   else {
                   dequipItem(name);
                   setFeet(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   }

                  break;
            case shield: 
                Item pshield = new Item(getShield());
                name = pshield.getName();
                Item cweapon = new Item(getWeapon());
                String cweapbodypart = cweapon.getBodypart();
                if (name == null){
                    name = "none";
                   }
                   
                   if ((name.equals("none")) & !(cweapbodypart.equals("twohand"))){
                   setShield(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                   Item tempshield = new Item(getShield());
                   String sname = tempshield.getName();
                   }
                   
                   else if (cweapbodypart.equals("twohand")){
                   System.out.println("dequip " + cweapon.getName() + " first, cannot use shield with two handed weapon");
                   }
                   
                   else if (cweapbodypart.equals("onehand")){
                   dequipItem(name);
                   setShield(item.getItemID());
                   System.out.println("\n" + item.getName()+ " equipped");
                }
                  break;
                  
            default: break;

            }
        }
    }

    public void dequipItem(String itemName) {
        ArrayList<Item> itemMap = searchItem(itemName, getStorage());
        if (!itemMap.isEmpty()) {
            Item item = itemMap.get(0);
            String bodypart = item.getBodypart();
            Part bpart = Part.valueOf(bodypart.toLowerCase());
            switch (bpart) {

            case onehand: setWeapon("hands");

            case twohand: setWeapon("hands");

            case head: setHead("none");

            case chest: setChest("shirt");

            case legs: setLegs("pants");

            case arms: setArms("none");

            case feet: setFeet("none");

            case shield: setShield("none");

            default: break;
            }
            System.out.println("\n" + item.getName()+" dequipped");
        }
    }

    public void inspectItem(String itemName) {
        ArrayList<Item> itemMap = searchItem(itemName, getStorage());
        if (itemMap.isEmpty()) {
            itemMap = searchItem(itemName, getLocation().getItems());
        }
        if (!itemMap.isEmpty()) {
            Item item = itemMap.get(0);
            item.display();
        } else {
            System.out.println("Item doesn't exist within your view.");
        }
    }

    public ILocation getLocation() {
        return location;
    }

    public void setLocation(ILocation location) {
        this.location = location;
    }

    public LocationType getLocationType(){
    	return getLocation().getLocationType();
    }

}
