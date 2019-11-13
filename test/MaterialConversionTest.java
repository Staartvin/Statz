//import me.staartvin.statz.util.StatzUtil;
//import org.bukkit.Material;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class MaterialConversionTest {
//
//    Map<String, Material> materialMap = new HashMap<>();
//
//    @Before
//    public void fillMap() {
//        materialMap.clear();
//
//        materialMap.put("1:0", Material.STONE);
//        materialMap.put("1:1", Material.GRANITE);
//        materialMap.put("1:2", Material.POLISHED_GRANITE);
//        materialMap.put("1:3", Material.DIORITE);
//        materialMap.put("1:4", Material.POLISHED_DIORITE);
//        materialMap.put("1:5", Material.ANDESITE);
//        materialMap.put("1:6", Material.POLISHED_ANDESITE);
//
//    }
//
//    public boolean isCorrectMaterial(Material testedMaterial, int itemId, int damageValue) {
//        return materialMap.get(itemId + ":" + damageValue).equals(testedMaterial);
//    }
//
//    // Test two conflicting queries
//    @Test
//    public void testExistingMaterials() {
//
//        for (Map.Entry<String, Material> entry : materialMap.entrySet()) {
//
//            String key = entry.getKey();
//
//            int itemId = Integer.parseInt(key.split(":")[0]);
//            int damageValue = Integer.parseInt(key.split(":")[1]);
//
//            Material foundMaterial = StatzUtil.findMaterial(itemId, damageValue);
//
//            Assert.assertNotNull(foundMaterial);
//
//            Assert.assertTrue(isCorrectMaterial(foundMaterial, itemId, damageValue));
//        }
//
//    }
//}
