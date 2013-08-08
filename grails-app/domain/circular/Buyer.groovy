package circular

class Buyer extends User {
	BuyerAccount account
	static constraints = {
		account(unique: true)
	}
	static mapping = {
/*		account cascade: 'all'*/
	}
}
