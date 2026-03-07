package com.jef.justenoughfakepixel.features.waypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jef.justenoughfakepixel.config.GuiElement;
import com.jef.justenoughfakepixel.config.RenderUtils;
import com.jef.justenoughfakepixel.config.TextRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WaypointGroupGui extends GuiElement {

    private static final int MAX_W   = 340;
    private static final int MAX_H   = 260;
    private static final int MARGIN  = 6;
    private static final int ROW_H   = 22;
    private static final int ROW_PAD = 4;
    private static final int BTN_W   = 42;
    private static final int BTN_H   = 14;
    private static final int SF_H    = 14;

    private int scrollOffset = 0;
    private GuiTextField searchField = null;
    private GuiTextField importNameField = null;
    private boolean importOpen = false;
    private GuiTextField createNameField = null;
    private boolean createOpen = false;

    private int panelW(ScaledResolution sr) { return Math.min(MAX_W, sr.getScaledWidth()  - MARGIN * 2); }
    private int panelH(ScaledResolution sr) { return Math.min(MAX_H, sr.getScaledHeight() - MARGIN * 2); }
    private int panelX(ScaledResolution sr) { return (sr.getScaledWidth()  - panelW(sr)) / 2; }
    private int panelY(ScaledResolution sr) { return (sr.getScaledHeight() - panelH(sr)) / 2; }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;

        int pw = panelW(sr);
        int ph = panelH(sr);
        int px = panelX(sr);
        int py = panelY(sr);

        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0x80101010);
        RenderUtils.drawFloatingRectDark(px, py, pw, ph, false);

        int curY = py + 5;

        // title
        RenderUtils.drawFloatingRectDark(px + 5, curY, pw - 10, 20, false);
        TextRenderUtils.drawStringCenteredScaledMaxWidth(
                EnumChatFormatting.LIGHT_PURPLE + "Waypoints",
                fr, px + pw / 2f, curY + 10, false, pw - 20, -1);
        curY += 24;

        WaypointState   state   = WaypointState.getInstance();
        WaypointStorage storage = WaypointStorage.getInstance();

        // status row
        if (state.hasGroup()) {
            String status = EnumChatFormatting.AQUA + "Loaded: "
                    + EnumChatFormatting.WHITE + state.loadedGroup.name
                    + EnumChatFormatting.GRAY  + " (" + state.size() + " wps)"
                    + "  wp " + (state.currentIndex + 1) + "/" + state.size();
            fr.drawStringWithShadow(status, px + 8, curY + 2, -1);
            int bx = px + pw - BTN_W - 8;
            drawButton(bx, curY, BTN_W, BTN_H, EnumChatFormatting.RED + "Unload", fr, isHovered(bx, curY, BTN_W, BTN_H));
        } else {
            fr.drawStringWithShadow(EnumChatFormatting.GRAY + "No group loaded", px + 8, curY + 2, -1);
        }
        curY += ROW_H;

        // search + import toggle
        int sfX = px + 8;
        int sfW = pw - 16 - BTN_W * 2 - 12;
        ensureSearchField(sfX, curY, sfW, SF_H, fr);
        searchField.xPosition = sfX; searchField.yPosition = curY;
        searchField.width = sfW;     searchField.height = SF_H;
        RenderUtils.drawFloatingRectDark(sfX - 2, curY - 2, sfW + 4, SF_H + 4, false);
        searchField.drawTextBox();

        int impTogX    = px + pw - BTN_W * 2 - 12;
        int createTogX = px + pw - BTN_W - 8;
        drawButton(impTogX, curY, BTN_W, SF_H,
                importOpen ? EnumChatFormatting.YELLOW + "Cancel" : EnumChatFormatting.AQUA + "Import",
                fr, isHovered(impTogX, curY, BTN_W, SF_H));
        drawButton(createTogX, curY, BTN_W, SF_H,
                createOpen ? EnumChatFormatting.YELLOW + "Cancel" : EnumChatFormatting.GREEN + "New",
                fr, isHovered(createTogX, curY, BTN_W, SF_H));
        curY += SF_H + 4;

        // import field
        if (importOpen) {
            int ifX = px + 8;
            int ifW = pw - 16 - BTN_W - 8;
            ensureImportField(ifX, curY, ifW, SF_H, fr);
            importNameField.xPosition = ifX; importNameField.yPosition = curY;
            importNameField.width = ifW;     importNameField.height = SF_H;
            RenderUtils.drawFloatingRectDark(ifX - 2, curY - 2, ifW + 4, SF_H + 4, false);
            importNameField.drawTextBox();
            if (importNameField.getText().isEmpty())
                fr.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "group name…", ifX + 2, curY + 2, -1);
            int goBtnX = px + pw - BTN_W - 8;
            drawButton(goBtnX, curY, BTN_W, SF_H, EnumChatFormatting.GREEN + "Import", fr, isHovered(goBtnX, curY, BTN_W, SF_H));
            curY += SF_H + 4;
        }

        // create field
        if (createOpen) {
            int cfX = px + 8;
            int cfW = pw - 16 - BTN_W - 8;
            ensureCreateField(cfX, curY, cfW, SF_H, fr);
            createNameField.xPosition = cfX; createNameField.yPosition = curY;
            createNameField.width = cfW;     createNameField.height = SF_H;
            RenderUtils.drawFloatingRectDark(cfX - 2, curY - 2, cfW + 4, SF_H + 4, false);
            createNameField.drawTextBox();
            if (createNameField.getText().isEmpty())
                fr.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Enter group name.", cfX + 2, curY + 2, -1);
            int createBtnX = px + pw - BTN_W - 8;
            drawButton(createBtnX, curY, BTN_W, SF_H, EnumChatFormatting.GREEN + "Create", fr, isHovered(createBtnX, curY, BTN_W, SF_H));
            curY += SF_H + 4;
        }

        // separator
        Gui.drawRect(px + 5, curY, px + pw - 5, curY + 1, 0xff404046);
        curY += 2;

        // group list
        int listTop    = curY;
        int listBottom = py + ph - 8;
        int visibleH   = Math.max(0, listBottom - listTop);

        String query = searchField.getText().trim().toLowerCase();
        List<WaypointGroup> all       = new ArrayList<>(storage.getGroups().values());
        List<WaypointGroup> groupList = new ArrayList<>();
        for (WaypointGroup g : all)
            if (query.isEmpty() || g.name.toLowerCase().contains(query)) groupList.add(g);

        int totalH    = groupList.size() * (ROW_H + ROW_PAD);
        int maxScroll = Math.max(0, totalH - visibleH);
        scrollOffset  = Math.max(0, Math.min(scrollOffset, maxScroll));

        int scale = sr.getScaleFactor();
        GlStateManager.pushMatrix();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor(px * scale, mc.displayHeight - listBottom * scale, pw * scale, visibleH * scale);

        int rowY = listTop - scrollOffset;
        for (WaypointGroup g : groupList) {
            if (rowY + ROW_H > listTop && rowY < listBottom)
                drawGroupRow(px, rowY, pw, g, state, fr);
            rowY += ROW_H + ROW_PAD;
        }

        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();

        if (groupList.isEmpty()) {
            String hint = query.isEmpty()
                    ? EnumChatFormatting.GRAY + "No groups saved  –  use /w create <n>"
                    : EnumChatFormatting.GRAY + "No groups match \"" + query + "\"";
            TextRenderUtils.drawStringCenteredScaledMaxWidth(hint, fr, px + pw / 2f, listTop + 20, false, pw - 20, -1);
        }

        // scrollbar
        if (maxScroll > 0) {
            float frac = (float) visibleH / totalH;
            int barH = Math.max(16, (int)(visibleH * frac));
            int barY = listTop + (int)((visibleH - barH) * ((float) scrollOffset / maxScroll));
            int barX = px + pw - 7;
            Gui.drawRect(barX, listTop, barX + 4, listBottom, 0xff202026);
            Gui.drawRect(barX, barY,    barX + 4, barY + barH, 0xff505056);
        }
    }

    private void drawGroupRow(int panelX, int y, int panelW, WaypointGroup g, WaypointState state, FontRenderer fr) {
        boolean isLoaded = state.loadedGroup != null && state.loadedGroup.name.equalsIgnoreCase(g.name);
        Gui.drawRect(panelX + 6, y, panelX + panelW - 6, y + ROW_H - 1, isLoaded ? 0x40006600 : 0x20ffffff);

        String label = (isLoaded ? EnumChatFormatting.GREEN : EnumChatFormatting.YELLOW) + g.name
                + EnumChatFormatting.GRAY + " (" + g.waypoints.size() + ")";
        TextRenderUtils.drawStringScaledMaxWidth(label, fr, panelX + 12, y + 6, false,
                panelW - 30 - BTN_W * 3 - 18, -1);

        int loadX   = panelX + panelW - BTN_W * 3 - 18;
        int exportX = panelX + panelW - BTN_W * 2 - 12;
        int delX    = panelX + panelW - BTN_W - 8;

        drawButton(loadX,   y + 4, BTN_W, BTN_H,
                isLoaded ? EnumChatFormatting.AQUA + "Reload" : EnumChatFormatting.GREEN + "Load",
                fr, isHovered(loadX, y + 4, BTN_W, BTN_H));
        drawButton(exportX, y + 4, BTN_W, BTN_H,
                EnumChatFormatting.YELLOW + "Export", fr, isHovered(exportX, y + 4, BTN_W, BTN_H));
        drawButton(delX,    y + 4, BTN_W, BTN_H,
                EnumChatFormatting.RED + "Delete",    fr, isHovered(delX,    y + 4, BTN_W, BTN_H));
    }

    private void drawButton(int x, int y, int w, int h, String label, FontRenderer fr, boolean hovered) {
        Gui.drawRect(x, y,         x + w, y + h, hovered ? 0xff3a3a46 : 0xff28282e);
        Gui.drawRect(x, y,         x + w, y + 1,     0xff505056);
        Gui.drawRect(x, y + h - 1, x + w, y + h,     0xff101010);
        TextRenderUtils.drawStringCenteredScaledMaxWidth(label, fr, x + w / 2f, y + h / 2f + 1, false, w - 4, -1);
    }

    @Override
    public boolean mouseInput(int mouseX, int mouseY) {
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            scrollOffset = Math.max(0, scrollOffset - (dWheel > 0 ? 20 : -20));
            return false;
        }

        if (!Mouse.getEventButtonState() || Mouse.getEventButton() != 0) return false;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int pw = panelW(sr);
        int ph = panelH(sr);
        int px = panelX(sr);
        int py = panelY(sr);

        WaypointState   state   = WaypointState.getInstance();
        WaypointStorage storage = WaypointStorage.getInstance();

        int curY = py + 5 + 24;

        if (state.hasGroup()) {
            int bx = px + pw - BTN_W - 8;
            if (inBounds(mouseX, mouseY, bx, curY, BTN_W, BTN_H)) { state.unload(); return true; }
        }
        curY += ROW_H;

        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, 0);

        // both toggle buttons sit on the same row as the search bar
        int impTogX    = px + pw - BTN_W * 2 - 12;
        int createTogX = px + pw - BTN_W - 8;
        if (inBounds(mouseX, mouseY, impTogX, curY, BTN_W, SF_H)) {
            importOpen = !importOpen;
            if (!importOpen) importNameField = null;
            return true;
        }
        if (inBounds(mouseX, mouseY, createTogX, curY, BTN_W, SF_H)) {
            createOpen = !createOpen;
            if (!createOpen) createNameField = null;
            return true;
        }
        curY += SF_H + 4;

        if (importOpen) {
            if (importNameField != null) importNameField.mouseClicked(mouseX, mouseY, 0);
            int goBtnX = px + pw - BTN_W - 8;
            if (inBounds(mouseX, mouseY, goBtnX, curY, BTN_W, SF_H)) { doImport(storage); return true; }
            curY += SF_H + 4;
        }

        if (createOpen) {
            if (createNameField != null) createNameField.mouseClicked(mouseX, mouseY, 0);
            int createBtnX = px + pw - BTN_W - 8;
            if (inBounds(mouseX, mouseY, createBtnX, curY, BTN_W, SF_H)) { doCreate(storage); return true; }
            curY += SF_H + 4;
        }

        curY += 2;
        int listTop    = curY;
        int listBottom = py + ph - 8;

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        List<WaypointGroup> all       = new ArrayList<>(storage.getGroups().values());
        List<WaypointGroup> groupList = new ArrayList<>();
        for (WaypointGroup g : all)
            if (query.isEmpty() || g.name.toLowerCase().contains(query)) groupList.add(g);

        int rowY = listTop - scrollOffset;
        for (WaypointGroup g : groupList) {
            if (rowY + ROW_H > listTop && rowY < listBottom) {
                int loadX   = px + pw - BTN_W * 3 - 18;
                int exportX = px + pw - BTN_W * 2 - 12;
                int delX    = px + pw - BTN_W - 8;

                if (inBounds(mouseX, mouseY, loadX, rowY + 4, BTN_W, BTN_H)) {
                    state.load(g); return true;
                }
                if (inBounds(mouseX, mouseY, exportX, rowY + 4, BTN_W, BTN_H)) {
                    GuiScreen.setClipboardString(exportSoopy(g)); return true;
                }
                if (inBounds(mouseX, mouseY, delX, rowY + 4, BTN_W, BTN_H)) {
                    if (state.loadedGroup != null && state.loadedGroup.name.equalsIgnoreCase(g.name)) state.unload();
                    storage.removeGroup(g.name);
                    storage.saveIfDirty();
                    return true;
                }
            }
            rowY += ROW_H + ROW_PAD;
        }

        return false;
    }

    @Override
    public boolean keyboardInput() {
        if (!Keyboard.getEventKeyState()) return false;

        int  key = Keyboard.getEventKey();
        char c   = Keyboard.getEventCharacter();

        boolean searchFocused = searchField     != null && searchField.isFocused();
        boolean importFocused = importNameField != null && importNameField.isFocused();

        if (!searchFocused && !importFocused && (createNameField == null || !createNameField.isFocused())) {
            if (key == Keyboard.KEY_DOWN) { scrollOffset += 10; return true; }
            if (key == Keyboard.KEY_UP)   { scrollOffset = Math.max(0, scrollOffset - 10); return true; }
        }

        boolean createFocused = createNameField != null && createNameField.isFocused();

        if (importFocused && key == Keyboard.KEY_RETURN) { doImport(WaypointStorage.getInstance()); return true; }
        if (createFocused && key == Keyboard.KEY_RETURN) { doCreate(WaypointStorage.getInstance()); return true; }
        if (searchFocused) { searchField.textboxKeyTyped(c, key);      return true; }
        if (importFocused) { importNameField.textboxKeyTyped(c, key);  return true; }
        if (createFocused) { createNameField.textboxKeyTyped(c, key);  return true; }

        return false;
    }

    private void doImport(WaypointStorage storage) {
        if (importNameField == null) return;
        String name = importNameField.getText().trim().toLowerCase();
        if (name.isEmpty()) return;
        String clip = GuiScreen.getClipboardString();
        if (clip == null || clip.trim().isEmpty()) return;
        List<WaypointPoint> wps = parseSoopy(clip.trim());
        if (wps == null || wps.isEmpty()) return;
        WaypointGroup g = storage.getGroup(name);
        if (g == null) g = new WaypointGroup(name);
        g.waypoints = wps;
        storage.putGroup(g);
        storage.saveIfDirty();
        importOpen = false;
        importNameField = null;
    }

    private void doCreate(WaypointStorage storage) {
        if (createNameField == null) return;
        String name = createNameField.getText().trim().toLowerCase();
        if (name.isEmpty() || storage.getGroup(name) != null) return;
        storage.putGroup(new WaypointGroup(name));
        storage.saveIfDirty();
        createOpen = false;
        createNameField = null;
    }

    private static String exportSoopy(WaypointGroup g) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WaypointPoint wp : g.waypoints) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("x", wp.x); m.put("y", wp.y); m.put("z", wp.z);
            m.put("r", 0); m.put("g", 1); m.put("b", 0);
            Map<String, Object> opts = new LinkedHashMap<>();
            opts.put("name", wp.name != null ? wp.name : "");
            m.put("options", opts);
            list.add(m);
        }
        return new GsonBuilder().create().toJson(list);
    }

    private static List<WaypointPoint> parseSoopy(String json) {
        try {
            if (json.startsWith("[")) {
                Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> raw = new Gson().fromJson(json, type);
                List<WaypointPoint> wps = new ArrayList<>();
                for (int i = 0; i < raw.size(); i++) {
                    Map<String, Object> m = raw.get(i);
                    double x = toDouble(m.get("x")), y = toDouble(m.get("y")), z = toDouble(m.get("z"));
                    String wpName = String.valueOf(i + 1);
                    if (m.containsKey("options")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> opts = (Map<String, Object>) m.get("options");
                        if (opts != null && opts.containsKey("name")) wpName = String.valueOf(opts.get("name"));
                    }
                    wps.add(new WaypointPoint(x, y, z, wpName));
                }
                wps.sort((a, b) -> {
                    try { return Integer.compare(Integer.parseInt(a.name), Integer.parseInt(b.name)); }
                    catch (NumberFormatException e) { return 0; }
                });
                return wps;
            }
            if (json.matches("(?s).*\\d.*")) {
                List<WaypointPoint> wps = new ArrayList<>();
                String[] rows = json.split("[\r\n]+");
                for (int i = 0; i < rows.length; i++) {
                    String[] parts = rows[i].trim().split("\\s+");
                    if (parts.length >= 3)
                        wps.add(new WaypointPoint(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), String.valueOf(i + 1)));
                }
                return wps.isEmpty() ? null : wps;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static double toDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        return Double.parseDouble(String.valueOf(o));
    }

    private void ensureSearchField(int x, int y, int w, int h, FontRenderer fr) {
        if (searchField == null) {
            searchField = new GuiTextField(0, fr, x, y, w, h);
            searchField.setMaxStringLength(64);
            searchField.setEnableBackgroundDrawing(false);
            searchField.setCanLoseFocus(true);
            searchField.setFocused(false);
        }
    }

    private void ensureCreateField(int x, int y, int w, int h, FontRenderer fr) {
        if (createNameField == null) {
            createNameField = new GuiTextField(2, fr, x, y, w, h);
            createNameField.setMaxStringLength(64);
            createNameField.setEnableBackgroundDrawing(false);
            createNameField.setCanLoseFocus(true);
            createNameField.setFocused(true);
        }
    }

    private void ensureImportField(int x, int y, int w, int h, FontRenderer fr) {
        if (importNameField == null) {
            importNameField = new GuiTextField(1, fr, x, y, w, h);
            importNameField.setMaxStringLength(64);
            importNameField.setEnableBackgroundDrawing(false);
            importNameField.setCanLoseFocus(true);
            importNameField.setFocused(true);
        }
    }

    private boolean inBounds(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private boolean isHovered(int x, int y, int w, int h) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int mx = Mouse.getX() * sr.getScaledWidth()  / Minecraft.getMinecraft().displayWidth;
        int my = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
        return inBounds(mx, my, x, y, w, h);
    }
}