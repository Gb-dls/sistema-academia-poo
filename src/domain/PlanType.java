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

    /* NOVO MeTODO TRADUTOR */
    public static PlanType fromInt(int valor) {
        for (PlanType tipo : PlanType.values()) {
            if (tipo.getOptionValue() == valor) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Opção de plano inválida: " + valor);
    }
}