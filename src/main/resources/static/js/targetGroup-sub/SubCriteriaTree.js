class SubCriteriaTree {
    constructor(key) {
        this.root = new CriteriaNode(key, null, null, null, 0, 0, 0, 0);
    }

    *preOrderTraversal(node = this.root) {
        yield node;
        if (node.children.length) {
            for (let child of node.children) {
                yield* this.preOrderTraversal(child);
            }
        }
    }

    *preOrderTraversalFromNode(node) {
        if (node.children.length) {
            for (let child of node.children) {
                yield* this.preOrderTraversal(child);
            }
        }
    }

    *postOrderTraversal(node = this.root) {
        if (node.children.length) {
            for (let child of node.children) {
                yield* this.postOrderTraversal(child);
            }
        }
        yield node;
    }

    insert(parentNodeKey, key) {
        for (let currentNode of this.preOrderTraversal()) {
            if (currentNode.key === parentNodeKey) {
                let numberOfChild = currentNode.children.length;
                let newNode = new CriteriaNode(key, currentNode, numberOfChild + 1, currentNode.level + 1, "", "", "", "");
                currentNode.children.push(newNode);
                return newNode;
            }
        }
        return undefined;
    }

    remove(key) {
        for (let node of this.preOrderTraversal()) {
            const filtered = node.children.filter(c => c.key !== key);
            if (filtered.length !== node.children.length) {
                node.children = filtered;
                return true;
            }
        }
        return false;
    }

    find(key) {
        for (let node of this.preOrderTraversal()) {
            if (node.key === key) return node;
        }
        return undefined;
    }

    printTree(){
        for (let currentNode of this.postOrderTraversal()) {
            console.log("currentNodeKey: " + currentNode.key);
        }
    }
}