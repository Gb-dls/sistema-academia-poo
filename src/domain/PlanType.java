package domain;

public enum PlanType {
    MONTHLY(1), QUARTERLY(2), SEMI_ANNUAL(3), ANNUAL(4);

    private final int optionValue;

    PlanType(int optionValue){
        this.optionValue = optionValue;
    }

    public int getOptionValue(){
        return optionValue;
    }

    // Converte o valor numérico do menu para o enum correspondente.
    // Usado em PlanMenu para mapear a escolha do usuário ao tipo de plano.
    // Lança IllegalArgumentException se o valor não corresponder a nenhuma opção.
    public static PlanType fromOptionValue(int value) {
        for (PlanType type : PlanType.values()) {
            if (type.getOptionValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid plan type option: " + value);
    }

}