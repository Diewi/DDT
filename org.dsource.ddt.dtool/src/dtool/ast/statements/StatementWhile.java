package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;

public class StatementWhile extends Statement {
	
	public final Expression condition;
	public final IStatement body;
	
	public StatementWhile(Expression condition, IStatement body) {
		this.condition = parentize(condition);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_WHILE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("while ");
		MissingParenthesesExpression.appendParenthesesExp(cp, condition);
		cp.append(body);
	}
	
}