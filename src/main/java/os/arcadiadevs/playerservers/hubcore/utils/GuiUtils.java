package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;

public class GuiUtils {

  public static void addBorder(SGMenu menu, int rows) {
    for (int i = 0; i < 9; i++) {
      menu.setButton(
          i,
          new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build())
      );
    }

    for (int i = 0; i < rows; i++) {
      menu.setButton(
          i * 9,
          new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build())
      );
    }

    for (int i = 0; i < rows; i++) {
      menu.setButton(
          i * 9 + 8,
          new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build())
      );
    }

    for (int i = (rows - 1) * 9; i < ((rows - 1) * 9) + 9; i++) {
      menu.setButton(
          i,
          new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build())
      );
    }
  }

}
