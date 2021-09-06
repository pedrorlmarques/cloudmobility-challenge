db.createUser(
    {
        user: "appointmentUser",
        pwd: "Pa$$w0rd123",
        roles: [
            {
                role: "readWrite",
                db: "appointmentdb"
            }
        ]
    }
);

//script to load the create user
db.dummy.insert([{
    "dummy": 1,
    "value": 1,
    "transactionType": "DEBIT",
    "created_date": {"$date": "2020-05-25T09:03:38.313Z"},
    "last_modified_date": {"$date": "2020-05-25T09:03:38.313Z"},
    "_class": "com.Dummy"
}]);
