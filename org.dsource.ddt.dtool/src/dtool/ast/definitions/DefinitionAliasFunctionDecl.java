package dtool.ast.definitions;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.Attribute;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of an alias, in the old syntax:
 * <code>alias BasicType Declarator ;</code>
 * when Declarator declares a function.
 * 
 * @see http://dlang.org/declaration.html#AliasDeclaration
 */
public class DefinitionAliasFunctionDecl extends CommonDefinition implements IStatement {
	
	public final ArrayView<Attribute> aliasedAttributes;
	public final Reference target;
	public final ArrayView<IFunctionParameter> fnParams;
	public final ArrayView<FunctionAttributes> fnAttributes;
	
	public DefinitionAliasFunctionDecl(Token[] comments, ArrayView<Attribute> aliasedAttributes, Reference target, 
		ProtoDefSymbol defId, ArrayView<IFunctionParameter> fnParams, ArrayView<FunctionAttributes> fnAttributes) {
		super(comments, defId);
		this.aliasedAttributes = parentize(aliasedAttributes);
		this.target = parentize(target);
		this.fnParams = parentizeI(fnParams);
		this.fnAttributes = fnAttributes;
		assertTrue(fnAttributes == null || fnParams != null);
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(fnParams);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ALIAS_FUNCTION_DECL;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, aliasedAttributes);
			TreeVisitor.acceptChildren(visitor, target);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, fnParams);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.appendList(aliasedAttributes, " ", true);
		cp.append(target, " ");
		cp.append(defname);
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendTokenList(fnAttributes, " ", true);
		cp.append(";");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return target.getTargetScope(moduleResolver); // XXX: Not correct for functional variant of alias
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " -> " + target.toStringAsElement();
	}
	
}