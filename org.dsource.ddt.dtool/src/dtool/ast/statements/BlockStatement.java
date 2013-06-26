package dtool.ast.statements;

import java.util.Iterator;
import java.util.List;

import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNode;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.api.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends CommonStatementList implements IScopeNode, IFunctionBody {
	
	public BlockStatement(ArrayView<IStatement> statements) {
		super(statements);
	}
	
	public BlockStatement() {
		super();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.BLOCK_STATEMENT;
	}
	
	@Override
	public Iterator<? extends IASTNode> getMembersIterator(IModuleResolver moduleResolver) {
		return super.getMembersIterator();
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
}