package dtool.ast;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.util.ArrayView;

public class NodeList2 extends ASTNeoNode {
	
	public final ArrayView<ASTNeoNode> nodes;
	
	public NodeList2(ArrayView<ASTNeoNode> nodes, SourceRange sr) {
		this.nodes = assertNotNull_(parentize(nodes));
		initSourceRange(sr);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.NODE_LIST;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, nodes);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		for (ASTNeoNode node : nodes) {
			cp.appendNode(node, "\n");
		}
	}
	
}