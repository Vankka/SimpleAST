package me.vankka.simpleast.core;

import me.vankka.simpleast.core.node.Node;
import me.vankka.simpleast.core.node.StyleNode;
import me.vankka.simpleast.core.node.TextNode;
import me.vankka.simpleast.core.parser.Parser;
import me.vankka.simpleast.core.simple.SimpleMarkdownRules;
import me.vankka.simpleast.core.util.TreeMatcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParserTest {

    private Parser<Object, Node<Object>> parser;
    private TreeMatcher treeMatcher;

    @Before
    public void setup() {
        parser = new Parser<>();
        parser.addRules(SimpleMarkdownRules.createSimpleMarkdownRules(true));
        treeMatcher = new TreeMatcher();
        treeMatcher.registerDefaultMatchers();
    }

    @After
    public void tearDown() {
        parser = null;
    }

    @Test
    public void testEmptyParse() throws Exception {
        final List<Node<Object>> ast = parser.parse("");
        Assert.assertTrue(ast.isEmpty());
    }

    @Test
    public void testParseFormattedText() throws Exception {
        final List<Node<Object>> ast = parser.parse("**bold**");

        final StyleNode boldNode = StyleNode.createWithText("bold", Collections.singletonList(TextStyle.BOLD));

        final List<? extends Node> model = Collections.singletonList(boldNode);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

    @Test
    public void testParseLeadingFormatting() throws Exception {
        final List<Node<Object>> ast = parser.parse("**bold** and not bold");


        final StyleNode boldNode = StyleNode.createWithText("bold", Collections.singletonList(TextStyle.BOLD));
        final TextNode trailingText = new TextNode(" and not bold");

        final List<? extends Node> model = Arrays.asList(boldNode, trailingText);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

    @Test
    public void testParseTrailingFormatting() throws Exception {
        final List<Node<Object>> ast = parser.parse("not bold **and bold**");

        final TextNode leadingText = new TextNode("not bold ");
        final StyleNode boldNode = StyleNode.createWithText("and bold", Collections.singletonList(TextStyle.BOLD));

        final List<? extends Node> model = Arrays.asList(leadingText, boldNode);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

    @Test
    public void testNestedFormatting() throws Exception {
//        final List<Node> ast = parser.parse("*** test1 ** test2 * test3 * test4 ** test5 ***");
        final List<Node<Object>> ast = parser.parse("**bold *and italics* and more bold**");
//        final List<Node> ast = parser.parse("______" +
//            "t"
//        + "______");

        final StyleNode<Object, ?> boldNode = new StyleNode<>(Collections.singletonList(TextStyle.BOLD));
        boldNode.addChild(new TextNode<>("bold "));
        boldNode.addChild(StyleNode.createWithText("and italics", Collections.singletonList(TextStyle.ITALICS)));
        boldNode.addChild(new TextNode<>(" and more bold"));

        final List<? extends Node> model = Collections.singletonList(boldNode);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

//    @Test
    public void testNewlineRule() {
        final List<Node<Object>> ast = parser.parse("Some text\n\n\n  \n\n\nnewline above");

        final List<? extends Node> model = Arrays.asList(
                new TextNode<>("Some text"),
                new TextNode<>("\n"),
                new TextNode<>("\n"),
                new TextNode<>("newline above"));
        Assert.assertTrue("actual " + ast, treeMatcher.matches(model, ast));
    }
}
