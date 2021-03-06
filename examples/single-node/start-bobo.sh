#!/bin/sh

# -----------------------------------------------------------------------------
# This script starts a single node testnet.

# The testnet is a simple small, but fully functional blockchain network for
# experiments: you can run transactions (programs), introspect storage and watch
# the network state via logs.

# To learn what happens when the testnet is started refer to
# doc/how-to-single-node.md .
# -----------------------------------------------------------------------------


# Use default configuration with following overrides in env vars
unset  TC_CONFIG_FILE

export TC_P2P_PORT=5221
export TC_RPC_PORT=46659

export TC_ABCI_PORT=46660
export TC_ABCI_SOCK="/tmp/pravda/bobo/abci.sock"

export TC_API_HOST='localhost'
export TC_API_PORT=8081

export TC_IS_VALIDATOR=true
export TC_DISTRIBUTION=true

export TC_DATA=/tmp/pravda/bobo
export TC_SEEDS=""

export TC_GENESIS_TIME=0001-01-01T00:00:00Z
export TC_GENESIS_CHAIN_ID=pravda-demo

export BOBO_WALLET=57EBFE6DE281407E6DA6B26B83BB8E71059216787C6939F1A40EFA2604B71289

export TC_PAYMENT_WALLET_ADDRESS=$BOBO_WALLET
export TC_PAYMENT_WALLET_PRIVATE_KEY=36C1D1607BEB2DAC03D64871F4C9831056686166434EA9B33008CBBEEA7E197157EBFE6DE281407E6DA6B26B83BB8E71059216787C6939F1A40EFA2604B71289

export TC_GENESIS_VALIDATORS="bobo:10:$BOBO_WALLET"

pravda node
