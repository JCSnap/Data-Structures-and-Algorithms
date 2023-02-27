/**
 * ScapeGoat Tree class
 *
 * This class contains some of the basic code for implementing a ScapeGoat tree.
 * This version does not include any of the functionality for choosing which node
 * to scapegoat.  It includes only code for inserting a node, and the code for rebuilding
 * a subtree.
 */
import com.sun.source.tree.Tree;

public class SGTree {

    // Designates which child in a binary tree
    enum Child {LEFT, RIGHT}

    /**
     * TreeNode class.
     *
     * This class holds the data for a node in a binary tree.
     *
     * Note: we have made things public here to facilitate problem set grading/testing.
     * In general, making everything public like this is a bad idea!
     *
     */
    public static class TreeNode {
        int key;
        public TreeNode left = null;
        public TreeNode right = null;

        TreeNode(int k) {
            key = k;
        }
    }

    // Root of the binary tree
    public TreeNode root = null;

    /**
     * Counts the number of nodes in the specified subtree
     *
     * @param node  the parent node, not to be counted
     * @param child the specified subtree
     * @return number of nodes
     */
    public static int countNodes(TreeNode node, Child child) {
        TreeNode nextNode = node;
        if (node == null) {
            return 0;
        }
        if (child == Child.LEFT) {
            nextNode = node.left;
        } else if (child == Child.RIGHT) {
            nextNode = node.right;
        }
        if (nextNode == null) {
            return 0;
        } else {
            return 1 + countNodes(nextNode, Child.LEFT) + countNodes(nextNode, Child.RIGHT);
        }
    }

    /**
     * Builds an array of nodes in the specified subtree
     *
     * @param node  the parent node, not to be included in returned array
     * @param child the specified subtree
     * @return array of nodes
     */
    public TreeNode[] enumerateNodes(TreeNode node, Child child) {
        int arrSize = countNodes(node, child);
        TreeNode[] arr = new TreeNode[arrSize];
        int[] pointer = new int[1];
        // pointer inside an array allows it to be "global" for all recursions
        pointer[0] = 0;
        TreeNode nextNode = node;
        if (child == Child.LEFT) {
            nextNode = node.left;
        } else if (child == Child.RIGHT) {
            nextNode = node.right;
        }
        recurse(nextNode, arr, pointer);
        return arr;
    }

    private static void recurse(TreeNode node, TreeNode[] arr, int[] pointer) {
        if (node == null) {
            // do nothing
        } else {
            recurse(node.left, arr, pointer);
            int curPointer = pointer[0];
            arr[curPointer] = node;
            pointer[0] = pointer[0] + 1;
            recurse(node.right, arr, pointer);
        }
    }

    /**
     * Builds a tree from the list of nodes
     * Returns the node that is the new root of the subtree
     *
     * @param nodeList ordered array of nodes
     * @return the new root node
     */

    public TreeNode buildTree(TreeNode[] nodeList) {

        return recurseBuild(nodeList, 0, nodeList.length - 1);
    }

    private TreeNode recurseBuild(TreeNode[] nodeList, int low, int high) {
        if (high < low || low > high) {
            return null;
        }
        if (low == high) {
            TreeNode midNode = nodeList[low];
            midNode.left = null;
            midNode.right = null;
            return midNode;
        }
        int mid = (low + high) / 2;
        TreeNode midNode = nodeList[mid];
        midNode.left = recurseBuild(nodeList, low, mid - 1);
        midNode.right = recurseBuild(nodeList, mid + 1, high);
        return midNode;
    }
    /* failed attempt
    public TreeNode buildTree(TreeNode[] nodeList) {
        if (nodeList.length == 0) {
            return null;
        } else if (nodeList.length == 1) {
            TreeNode midNode = nodeList[0];
            midNode.left = null;
            midNode.right = null;
            return nodeList[0];
        } else if (nodeList.length == 2) {
            TreeNode midNode = nodeList[0];
            TreeNode child = nodeList[1];
            child.left = null;
            child.right = null;
            midNode.left = null;
            midNode.right = child;
            return midNode;
        }
        int low = 0;
        int high = nodeList.length - 1;
        int mid = (low + high) / 2;
        TreeNode midNode = nodeList[mid];
        if (nodeList.length % 2 == 0) {
            TreeNode second = nodeList[(low + high + 1) / 2];
            if (midNode.left == null) {
                midNode = second;
            } else {
            }
        } else {
            // continue
        }
        TreeNode[] leftNodeList = splitArray(nodeList, low, mid - 1);
        TreeNode[] rightNodeList = splitArray(nodeList, mid + 1, high);
        TreeNode left = buildTree(leftNodeList);
        TreeNode right = buildTree(rightNodeList);
        midNode.left = left;
        midNode.right = right;
        return midNode;
    }

    private TreeNode[] splitArray(TreeNode[] nodeList, int start, int end) {
        TreeNode[] newList = new TreeNode[end - start + 1];
        for (int i = start; i <= end; i = i + 1) {
            newList[i - start] = nodeList[i];
        }
        return newList;
    }
*/
    /**
    * Rebuilds the specified subtree of a node
    * 
    * @param node the part of the subtree to rebuild
    * @param child specifies which child is the root of the subtree to rebuild
    */
    public void rebuild(TreeNode node, Child child) {
        // Error checking: cannot rebuild null tree
        if (node == null) return;
        // First, retrieve a list of all the nodes of the subtree rooted at child
        TreeNode[] nodeList = enumerateNodes(node, child);
        TreeNode newChild = buildTree(nodeList);
        TreeNode[] enu = enumerateNodes(newChild, Child.LEFT);
        // Finally, replace the specified child with the new subtree
        if (child == Child.LEFT) {
            node.left = newChild;
        } else if (child == Child.RIGHT) {
            node.right = newChild;
        }
    }

    /**
    * Inserts a key into the tree
    *
    * @param key the key to insert
    */
    public void insert(int key) {
        if (root == null) {
            root = new TreeNode(key);
            return;
        }

        TreeNode node = root;

        while (true) {
            if (key <= node.key) {
                if (node.left == null) break;
                node = node.left;
            } else {
                if (node.right == null) break;
                node = node.right;
            }
        }

        if (key <= node.key) {
            node.left = new TreeNode(key);
        } else {
            node.right = new TreeNode(key);
        }
    }


    // Simple main function for debugging purposes
    public static void main(String[] args) {


        SGTree tree = new SGTree();
        for (int i = 0; i < 100; i++) {
            tree.insert(i);
        }
        tree.rebuild(tree.root, Child.RIGHT);

    }
}
