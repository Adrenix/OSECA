package mod.adrenix.oldswing.config.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.adrenix.oldswing.config.gui.screen.CustomScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.Item;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ItemSuggestionHelper
{
    private final Minecraft minecraft;
    private final CustomScreen screen;
    private final TextFieldWidget input;
    private final FontRenderer font;
    private final int suggestionLineLimit;
    private final int fillColor;
    private final List<IReorderingProcessor> listOfItems = Lists.newArrayList();
    private ParseResults<ISuggestionProvider> currentParse;
    private CompletableFuture<Suggestions> pendingSuggestions;
    private ItemSuggestions suggestions;
    private boolean allowSuggestions;
    private boolean keepSuggestions;

    public ItemSuggestionHelper(Minecraft minecraft, CustomScreen screen, TextFieldWidget input, FontRenderer font, int suggestionLineLimit, int fillColor)
    {
        this.minecraft = minecraft;
        this.screen = screen;
        this.input = input;
        this.font = font;
        this.suggestionLineLimit = suggestionLineLimit;
        this.fillColor = fillColor;
    }

    @Nullable
    public Item getItem()
    {
        ItemParser parser = new ItemParser(new StringReader(this.input.getValue()), false);
        try
        {
            parser.readItem();
        }
        catch (CommandSyntaxException e)
        {
            // Don't need to handle this since there will obviously be instances of invalid input.
        }

        return parser.getItem();
    }

    public boolean isSuggesting()
    {
        return this.suggestions != null;
    }

    @Nullable
    private static String calculateSuggestionSuffix(String in, String suggest)
    {
        return suggest.startsWith(in) ? suggest.substring(in.length()) : null;
    }

    public void setAllowSuggestions(boolean flag)
    {
        this.allowSuggestions = flag;

        if (!flag)
            this.suggestions = null;
    }

    public boolean keyPressed(int key)
    {
        if (this.suggestions != null && this.suggestions.keyPressed(key))
            return true;
        else if (this.screen.getFocused() == this.input && key == 258)
        {
            this.showSuggestions();
            return true;
        }
        else
            return false;
    }


    public boolean mouseScrolled(double v)
    {
        return this.suggestions != null && this.suggestions.mouseScrolled(MathHelper.clamp(v, -1.0D, 1.0D));
    }

    public boolean mouseClicked(double x, double y)
    {
        return this.suggestions != null && this.suggestions.mouseClicked((int) x, (int) y);
    }

    public void showSuggestions()
    {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone())
        {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty())
            {
                int x = this.input.x;
                int y = this.input.y + 20;
                int width = this.input.getWidth() + 1;
                this.suggestions = new ItemSuggestionHelper.ItemSuggestions(x, y, width, this.sortSuggestions(suggestions));
            }
        }
    }

    private List<Suggestion> sortSuggestions(Suggestions suggestions)
    {
        String check = this.input.getValue().substring(0, this.input.getCursorPosition());
        String path = check.toLowerCase(Locale.ROOT);
        List<Suggestion> sorted = Lists.newArrayList();
        List<Suggestion> remainder = Lists.newArrayList();

        for (Suggestion suggestion : suggestions.getList())
        {
            if (!suggestion.getText().startsWith(check) && !suggestion.getText().startsWith("minecraft:" + path))
                remainder.add(suggestion);
            else
                sorted.add(suggestion);
        }

        sorted.addAll(remainder);
        return sorted;
    }

    public void resetInputField()
    {
        this.input.setValue("");
        this.resetItemSuggestions();
    }

    private void resetItemSuggestions()
    {
        this.input.setSuggestion(null);
        this.suggestions = null;
    }

    public void updateItemSuggestions()
    {
        if (this.input.getValue().length() == 0)
        {
            this.resetItemSuggestions();
            return;
        }

        String in = this.input.getValue();
        StringReader reader = new StringReader(in);
        SuggestionsBuilder builder = new SuggestionsBuilder(in, 0);
        ItemParser parser = new ItemParser(reader, false);

        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(in))
            this.currentParse = null;

        if (!this.keepSuggestions)
            this.resetItemSuggestions();

        this.listOfItems.clear();

        try
        {
            parser.parse();
        }
        catch (CommandSyntaxException e)
        {
            // Unnecessary to handle this exception.
        }

        this.pendingSuggestions = parser.fillSuggestions(builder, ITagCollection.empty());
        this.pendingSuggestions.thenRun(() ->{
            if (this.pendingSuggestions.isDone())
                this.updateItemInfo();
        });
    }

    private void updateItemInfo()
    {
        if (this.listOfItems.isEmpty())
            this.fillItemSuggestions();

        this.suggestions = null;

        if (!this.listOfItems.isEmpty())
            if (this.allowSuggestions)
                this.showSuggestions();
    }

    private void fillItemSuggestions()
    {
        List<IReorderingProcessor> list = Lists.newArrayList();
        List<Suggestion> suggestions;
        Style style = Style.EMPTY.withColor(TextFormatting.GRAY);
        int i = 0;
        int count = 0;

        try
        {
            suggestions = this.pendingSuggestions.get().getList();
        }
        catch (ExecutionException | InterruptedException e)
        {
            // Unnecessary to handle these errors so just exit the method.
            return;
        }

        for (Suggestion suggestion : suggestions)
        {
            if (suggestion.getText().equals("{"))
                continue;

            list.add(IReorderingProcessor.forward(suggestion.getText(), style));
            i = Math.max(i, this.font.width(suggestion.getText()));

            count++;
            if (count >= this.suggestionLineLimit)
                break;
        }

        if (!list.isEmpty())
            this.listOfItems.addAll(list);
    }

    public void render(MatrixStack matrix, int x, int y)
    {
        if (this.suggestions != null)
            this.suggestions.render(matrix, x, y);

        if (this.screen.getAddItemButton() != null)
            this.screen.getAddItemButton().active = this.getItem() != null;
    }

    public class ItemSuggestions
    {
        private final Rectangle2d rectangle;
        private final String originalContents;
        private final List<Suggestion> suggestionList;
        private int offset;
        private int current;
        private Vector2f lastMouse = Vector2f.ZERO;
        private boolean tabCycles;

        public ItemSuggestions(int x, int y, int width, List<Suggestion> suggestionList)
        {
            this.rectangle = new Rectangle2d(x - 1, y, width + 1, Math.min(suggestionList.size(), ItemSuggestionHelper.this.suggestionLineLimit) * 12);
            this.originalContents = ItemSuggestionHelper.this.input.getValue();
            this.suggestionList = suggestionList;
            this.select(0);
        }

        public void render(MatrixStack matrix, int x, int y)
        {
            int i = Math.min(this.suggestionList.size(), ItemSuggestionHelper.this.suggestionLineLimit);

            boolean isScrolledAbove = this.offset > 0;
            boolean isScrolledBelow = this.suggestionList.size() > this.offset + 1;
            boolean isScrolled = isScrolledAbove || isScrolledBelow;
            boolean isMouseChanged = this.lastMouse.x != (float) x || this.lastMouse.y != (float) y;

            if (isMouseChanged)
                this.lastMouse = new Vector2f((float) x, (float) y);

            if (isScrolled)
            {
                AbstractGui.fill(matrix, this.rectangle.getX(), this.rectangle.getY() - 1, this.rectangle.getX() + this.rectangle.getWidth(), this.rectangle.getY(), ItemSuggestionHelper.this.fillColor);
                AbstractGui.fill(matrix, this.rectangle.getX(), this.rectangle.getY() + this.rectangle.getHeight(), this.rectangle.getX() + this.rectangle.getWidth(), this.rectangle.getY() + this.rectangle.getHeight() + 1, ItemSuggestionHelper.this.fillColor);

                if (isScrolledAbove)
                {
                    for (int k = 0; k < this.rectangle.getWidth(); k++)
                        if (k % 2 == 0)
                            AbstractGui.fill(matrix, this.rectangle.getX() + k, this.rectangle.getY() - 1, this.rectangle.getX() + k + 1, this.rectangle.getY(), -1);
                }

                if (isScrolledBelow && this.suggestionList.size() > ItemSuggestionHelper.this.suggestionLineLimit)
                {
                    for (int dx = 0; dx < this.rectangle.getWidth(); dx++)
                        if (dx % 2 == 0)
                            AbstractGui.fill(matrix, this.rectangle.getX() + dx, this.rectangle.getY() + this.rectangle.getHeight(), this.rectangle.getX() + dx + 1, this.rectangle.getY() + this.rectangle.getHeight() + 1, -1);
                }
            }

            boolean isHovering = false;

            for (int l = 0; l < i; l++)
            {
                Suggestion suggestion = this.suggestionList.get(l + this.offset);
                AbstractGui.fill(matrix, this.rectangle.getX(), this.rectangle.getY() + 12 * l, this.rectangle.getX() + this.rectangle.getWidth(), this.rectangle.getY() + 12 * l + 12, ItemSuggestionHelper.this.fillColor);
                if (x > this.rectangle.getX() && x < this.rectangle.getX() + this.rectangle.getWidth() && y > this.rectangle.getY() + 12 * l && y < this.rectangle.getY() + 12 * l + 12)
                {
                    if (isMouseChanged)
                        this.select(l + this.offset);

                    isHovering = true;
                }

                ItemSuggestionHelper.this.font.drawShadow(matrix, suggestion.getText(), (float) (this.rectangle.getX() + 1), (float) (this.rectangle.getY() + 2 + 12 * l), l + this.offset == this.current ? -256 : -5592406);
            }

            if (isHovering)
            {
                Message message = this.suggestionList.get(this.current).getTooltip();
                if (message != null)
                    ItemSuggestionHelper.this.screen.renderTooltip(matrix, TextComponentUtils.fromMessage(message), x, y);
            }
        }

        public boolean mouseClicked(int x, int y)
        {
            if (!this.rectangle.contains(x, y))
                return false;
            else
            {
                int i = (y - this.rectangle.getY()) / 12 + this.offset;
                if (i >= 0 && i < this.suggestionList.size())
                {
                    this.select(i);
                    this.useSuggestion();
                }

                return true;
            }
        }

        public boolean mouseScrolled(double dy)
        {
            int i = (int) (ItemSuggestionHelper.this.minecraft.mouseHandler.xpos() * (double) ItemSuggestionHelper.this.minecraft.getWindow().getGuiScaledWidth() / (double) ItemSuggestionHelper.this.minecraft.getWindow().getScreenWidth());
            int j = (int) (ItemSuggestionHelper.this.minecraft.mouseHandler.ypos() * (double) ItemSuggestionHelper.this.minecraft.getWindow().getGuiScaledHeight() / (double) ItemSuggestionHelper.this.minecraft.getWindow().getScreenHeight());

            if (this.rectangle.contains(i, j))
            {
                this.offset = MathHelper.clamp((int) ((double) this.offset - dy), 0, Math.max(this.suggestionList.size() - ItemSuggestionHelper.this.suggestionLineLimit, 0));
                return true;
            }
            else
                return false;
        }

        public boolean keyPressed(int key)
        {
            if (key == 265)
            {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            }
            else if (key == 264)
            {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            }
            else if (key == 258)
            {
                if (this.tabCycles)
                    this.cycle(Screen.hasShiftDown() ? -1 : 1);

                this.useSuggestion();
                return true;
            }
            else if (key == 256)
            {
                this.hide();
                return true;
            }
            else
                return false;
        }

        public void cycle(int increment)
        {
            this.select(this.current + increment);
            int i = this.offset;
            int j = this.offset + ItemSuggestionHelper.this.suggestionLineLimit - 1;

            if (this.current < i)
                this.offset = MathHelper.clamp(this.current, 0, Math.max(this.suggestionList.size() - ItemSuggestionHelper.this.suggestionLineLimit, 0));
            else if (this.current > j)
                this.offset = MathHelper.clamp(this.current + 1 - ItemSuggestionHelper.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - ItemSuggestionHelper.this.suggestionLineLimit, 0));
        }

        public void select(int current)
        {
            this.current = current;

            if (this.current < 0)
                this.current += this.suggestionList.size();

            if (this.current >= this.suggestionList.size())
                this.current -= this.suggestionList.size();

            Suggestion suggestion = this.suggestionList.get(this.current);
            ItemSuggestionHelper.this.input.setSuggestion(ItemSuggestionHelper.calculateSuggestionSuffix(ItemSuggestionHelper.this.input.getValue(), suggestion.apply(this.originalContents)));
        }

        public void useSuggestion()
        {
            Suggestion suggestion = this.suggestionList.get(this.current);
            ItemSuggestionHelper.this.keepSuggestions = true;
            ItemSuggestionHelper.this.input.setValue(suggestion.apply(this.originalContents));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            ItemSuggestionHelper.this.input.setCursorPosition(i);
            ItemSuggestionHelper.this.input.setHighlightPos(i);
            this.select(this.current);
            ItemSuggestionHelper.this.keepSuggestions = false;
            this.tabCycles = true;
        }

        public void hide()
        {
            ItemSuggestionHelper.this.suggestions = null;
        }
    }
}
