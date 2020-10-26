package dev.vankka.simpleast.core;

import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.StyleNode;
import dev.vankka.simpleast.core.node.TextNode;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.simple.SimpleMarkdownRules;
import dev.vankka.simpleast.core.util.TreeMatcher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParserTest {

    private Parser<Object, Node<Object>, Object> parser;
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
        final List<Node<Object>> ast = parser.parse("", null);
        Assert.assertTrue(ast.isEmpty());
    }

    @Test
    public void testParseFormattedText() throws Exception {
        final List<Node<Object>> ast = parser.parse("**bold**", null);

        final StyleNode boldNode = StyleNode.Companion.createWithText("bold", Collections.singletonList(new TextStyle(TextStyle.Type.BOLD)));

        final List<? extends Node> model = Collections.singletonList(boldNode);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

    @Test
    public void testParseLeadingFormatting() throws Exception {
        final List<Node<Object>> ast = parser.parse("**bold** and not bold", null);

        final StyleNode boldNode = StyleNode.Companion.createWithText("bold", Collections.singletonList(new TextStyle(TextStyle.Type.BOLD)));
        final TextNode trailingText = new TextNode(" and not bold");

        final List<? extends Node> model = Arrays.asList(boldNode, trailingText);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

    @Test
    public void testParseTrailingFormatting() throws Exception {
        final List<Node<Object>> ast = parser.parse("not bold **and bold**", null);

        final TextNode leadingText = new TextNode("not bold ");
        final StyleNode boldNode = StyleNode.Companion.createWithText("and bold", Collections.singletonList(new TextStyle(TextStyle.Type.BOLD)));

        final List<? extends Node> model = Arrays.asList(leadingText, boldNode);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

    @Test
    public void testNestedFormatting() throws Exception {
//        final List<Node> ast = parser.parse("*** test1 ** test2 * test3 * test4 ** test5 ***");
        final List<Node<Object>> ast = parser.parse("**bold *and italics* and more bold**", null);
//        final List<Node> ast = parser.parse("______" +
//            "t"
//        + "______");

        final StyleNode<Object, ?> boldNode = new StyleNode<>(Collections.singletonList(new TextStyle(TextStyle.Type.BOLD)));
        boldNode.addChild(new TextNode<>("bold "));
        boldNode.addChild(StyleNode.Companion.createWithText("and italics",
                Collections.singletonList(new TextStyle(TextStyle.Type.ITALICS))));
        boldNode.addChild(new TextNode<>(" and more bold"));

        final List<? extends Node> model = Collections.singletonList(boldNode);
        Assert.assertTrue(treeMatcher.matches(model, ast));
    }

//    @Test
    public void testNewlineRule() {
        final List<Node<Object>> ast = parser.parse("Some text\n\n\n  \n\n\nnewline above", null);

        final List<? extends Node> model = Arrays.asList(
                new TextNode<>("Some text"),
                new TextNode<>("\n"),
                new TextNode<>("\n"),
                new TextNode<>("newline above"));
        Assert.assertTrue("actual " + ast, treeMatcher.matches(model, ast));
    }
}
