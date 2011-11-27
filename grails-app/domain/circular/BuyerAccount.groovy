package circular

class BuyerAccount extends Account { 
    static belongsTo = [Buyer] 
    Buyer buyer 
}