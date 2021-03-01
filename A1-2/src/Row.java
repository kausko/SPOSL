public class Row {
	String symbol;
	int address,index;
	public String getSymbol() {
		return symbol;
	}
	public Row(String symbol, int address) {
		super();
		this.symbol = symbol;
		this.address = address;
		index=0;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Row(String symbol, int address, int index) {
		super();
		this.symbol = symbol;
		this.address = address;
		this.index = index;
	}
	public int getAddress() {
		return address;
	}
	public void setAddress(int address) {
		this.address = address;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "Row{" +
				"symbol='" + symbol + '\'' +
				", address=" + address +
				", index=" + index +
				'}';
	}
}