/**
 * ScapeGoat Tree class
 * <p>
 * This class contains some basic code for implementing a ScapeGoat tree. This version does not include any of the
 * functionality for choosing which node to scapegoat. It includes only code for inserting a node, and the code for
 * rebuilding a subtree.
 */

public class SGTree {

    // Designates which child in a binary tree
    enum Child {LEFT, RIGHT}

    /**
     * TreeNode class.
     * <p>
     * This class holds the data for a node in a binary tree.
     * <p>
     * Note: we have made things public here to facilitate problem set grading/testing. In general, making everything
     * public like this is a bad idea!
     */
    public static class TreeNode {
        int key;
        public TreeNode left = null;
        public TreeNode right = null;

        int weight;

        TreeNode(int k) {
            key = k;
            this.weight = 1;
        }
    }

    // Root of the binary tree
    public TreeNode root = null;

    /**
     * Counts the number of nodes in the specified subtree.
     *
     * @param node  the parent node, not to be counted
     * @param child the specified subtree
     * @return number of nodes
     */
    public int countNodes(TreeNode node, Child child) {
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
     * Builds an array of nodes in the specified subtree.
     *
     * @param node  the parent node, not to be included in returned array
     * @param child the specified subtree
     * @return array of nodes
     */
    TreeNode[] enumerateNodes(TreeNode node, Child child) {
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
     * Builds a tree from the list of nodes Returns the node that is the new root of the subtree
     *
     * @param nodeList ordered array of nodes
     * @return the new root node
     */
    TreeNode buildTree(TreeNode[] nodeList) {
        return recurseBuild(nodeList, 0, nodeList.length - 1);
    }

    private TreeNode recurseBuild(TreeNode[] nodeList, int low, int high) {
        if (high < low || low > high) {
            return null;
        }
        if (low == high) {
            TreeNode midNode = nodeList[low];
            midNode.weight = 1;
            midNode.left = null;
            midNode.right = null;
            return midNode;
        }
        int mid = (low + high) / 2;
        TreeNode midNode = nodeList[mid];
        midNode.left = recurseBuild(nodeList, low, mid - 1);
        midNode.right = recurseBuild(nodeList, mid + 1, high);
        if (midNode.left == null && midNode.right == null) {
            midNode.weight = 1;
        } else if (midNode.left == null) {
            midNode.weight = midNode.right.weight + 1;
        } else if (midNode.right == null) {
            midNode.weight = midNode.left.weight + 1;
        } else {
            midNode.weight = midNode.left.weight + midNode.right.weight + 1;
        }
        return midNode;
    }

    /**
     * Determines if a node is balanced. If the node is balanced, this should return true. Otherwise, it should return
     * false. A node is unbalanced if either of its children has weight greater than 2/3 of its weight.
     *
     * @param node a node to check balance on
     * @return true if the node is balanced, false otherwise
     */
    public boolean checkBalance(TreeNode node) {
        if (node == null) {
            return true;
        }
        int leftWeight = 0;
        int rightWeight = 0;
        if (!(node.left == null)) {
            leftWeight = node.left.weight;
        }
        if (!(node.right == null)) {
            rightWeight = node.right.weight;
        }
        int threshold = 2 * node.weight / 3;
        return leftWeight <= threshold && rightWeight <= threshold;
    }

    /**
     * Rebuilds the specified subtree of a node.
     *
     * @param node  the part of the subtree to rebuild
     * @param child specifies which child is the root of the subtree to rebuild
     */
    public void rebuild(TreeNode node, Child child) {
        // Error checking: cannot rebuild null tree
        if (node == null) return;
        // First, retrieve a list of all the nodes of the subtree rooted at child
        TreeNode[] nodeList = enumerateNodes(node, child);
        // Then, build a new subtree from that list
        TreeNode newChild = buildTree(nodeList);
        // Finally, replace the specified child with the new subtree
        if (child == Child.LEFT) {
            node.left = newChild;
        } else if (child == Child.RIGHT) {
            node.right = newChild;
        }
    }

    /**
     * Inserts a key into the tree.
     *
     * @param key the key to insert
     */
    public void insert(int key) {
        if (root == null) {
            root = new TreeNode(key);
            return;
        }
        TreeNode node = root;
        int maxHeight = root.weight + 1;
        TreeNode[] path = new TreeNode[maxHeight]; // keep track of path
        int point = 0;
        while (true) {
            if (key <= node.key) {
                TreeNode cur = node.left;
                node.weight++;
                path[point] = node;
                //System.out.println("Added node with key: " + node.key);
                point++;
                if (cur == null) {
                    break;
                } else {
                    node = node.left;
                }
            } else {
                TreeNode cur = node.right;
                node.weight++;
                path[point] = node;
                //System.out.println("Added node with key: " + node.key);
                point++;
                if (cur == null) {
                    break;
                } else {
                    node = node.right;
                }
            }
        }
        if (key <= node.key) {
            node.left = new TreeNode(key);
        } else {
            node.right = new TreeNode(key);
        }
        for (int i = 0; i < path.length; i++) {
            TreeNode curNode = path[i];
            if (curNode == null) {
                break;
            }
            if (!checkBalance(curNode)) {
                //System.out.println("Beep Boop, UNBALANCED!");
                if (i > 0) { // not root (root will always be at index 0)
                    TreeNode parentNode = path[i-1];
                    Child child = Child.LEFT;
                    if (curNode.key <= parentNode.key) {
                        // do nothing
                    } else {
                        child = Child.RIGHT;
                    }
                    rebuild(parentNode, child);
                    //System.out.println("Rebuilt node with key: " + curNode.key);
                } else {
                    // do nothing
                }
                if (i > 0) { // root does not count
                    break;
                }
            }
        }
    }

    // Simple main function for debugging purposes
    public static void main(String[] args) {
        SGTree tree = new SGTree();
        tree.insert(10);
        tree.insert(5);
        tree.insert(14);
        tree.insert(15);
        tree.insert(16);
        tree.insert(17);
        tree.insert(17);
        //int i = tree.countNodes(tree.root.right, Child.LEFT);
        //System.out.println(tree.root.right.right.right.key);
        //tree.rebuild(tree.root, Child.RIGHT);
    }
}
