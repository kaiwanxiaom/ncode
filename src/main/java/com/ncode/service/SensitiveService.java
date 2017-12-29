package com.ncode.service;

import com.ncode.util.DiscussUtil;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DiscussUtil.class);

    private class TrieNode {
        private boolean end = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();

        void addSubNode(Character key, TrieNode trieNode) {
            subNodes.put(key, trieNode);
        }

        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeyWordEnd() {
            return end;
        }

        void setKeyWordEnd() {
            end = true;
        }
    }

    /**
     * 根节点
     */
    private TrieNode rootNode = new TrieNode();

    private static final String DEFAULT_WORD = "***";

    /**
     * 判断是否是符号
     * @param c 字符
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF); // 东南亚字符 0x2E80-0x9FFF
    }


    /**
     * 过滤敏感词
     * @param text 原内容
     * @return 过滤内容
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);

            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                position++;
                begin = position;
                result.append(DEFAULT_WORD);
                tempNode = rootNode;
            } else {
                position++;
            }

        }

        result.append(text.substring(begin));

        return result.toString();
    }

    /**
     * 添加敏感词
     * @param lineText 敏感词
     */
    private void addWord(String lineText) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineText.length(); ++i) {
            Character c = lineText.charAt(i);

            TrieNode node = tempNode.subNodes.get(c);

            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineText.length() - 1) {
                tempNode.end = true;
            }
        }
    }

    /**
     * 启动Bean之前加载敏感词文件
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("sensitivewords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(read);
            String lineText;
            while ((lineText = br.readLine()) != null) {
                addWord(lineText);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件错误" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SensitiveService sensitiveService = new SensitiveService();
        sensitiveService.addWord("色情");
        sensitiveService.addWord("淫秽");
        System.out.println(sensitiveService.filter("淫_秽"));
    }
}
