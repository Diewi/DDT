package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeList;
import dtool.ast.definitions.DefinitionAggregate.IAggregateBody;
import dtool.refmodel.IScopeNode;
import dtool.util.ArrayView;

public class DeclList extends NodeList<ASTNode> implements IAggregateBody, IScopeNode {
	
	public DeclList(ArrayView<ASTNode> nodes) {
		super(nodes);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECL_LIST;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(nodes, "\n", true);
	}
	
}