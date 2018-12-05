//
// Created by jetbrains on 20.07.2018.
//

#include "Identities.h"

Identities::IdKind Identities::SERVER = IdKind::Server;
Identities::IdKind Identities::CLIENT = IdKind::Client;

Identities::Identities(Identities::IdKind dynamicKind) : id_acc(dynamicKind == IdKind::Client ? BASE_CLIENT_ID : BASE_SERVER_ID) {}

RdId Identities::next(const RdId &parent) const {
    RdId result = parent.mix(id_acc);
    id_acc += 2;
    return result;
}

hash_t getPlatformIndependentHash(std::string const &that, hash_t initial) {
//    std::cerr << that << " " << initial << std::endl;
    for (auto c : that) {
        initial = initial * HASH_FACTOR + static_cast<hash_t>(c);
    }
//    std::cerr << initial << std::endl;
    return initial;
}

hash_t getPlatformIndependentHash(int32_t const &that, hash_t initial) {
    return initial * HASH_FACTOR + (that + 1);
}

hash_t getPlatformIndependentHash(int64_t const &that, hash_t initial) {
    return initial * HASH_FACTOR + (that + 1);
}
